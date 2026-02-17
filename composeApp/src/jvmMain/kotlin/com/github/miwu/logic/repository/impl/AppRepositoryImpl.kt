package com.github.miwu.logic.repository.impl

import com.github.miwu.ktx.MiotHomeClient
import com.github.miwu.ktx.MiotUserClient
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.datastore.isLogin
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.logic.state.LoginState
import fr.haan.resultat.Resultat
import fr.haan.resultat.toResultat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import miwu.miot.client.MiotHomeClient
import miwu.miot.client.MiotUserClient
import miwu.miot.exception.MiotAuthException
import miwu.miot.exception.MiotClientException
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.MiotUserInfo.UserInfo
import miwu.miot.provider.MiotLoginProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.collections.emptyList

@OptIn(ExperimentalSerializationApi::class)
class AppRepositoryImpl : KoinComponent, AppRepository {
    private val appSetting: AppSetting by inject()
    private val scope: CoroutineScope by inject()
    private val deviceRepository: CacheRepository by inject()
    private val dataStore: MiotUserDataStore by inject()
    private val loginProvider: MiotLoginProvider by inject()
    private var miotUserClient: MiotUserClient? = null
    private var miotHomeClient: MiotHomeClient? = null
    private var currentHomeId
        get() = runBlocking { appSetting.homeId.getValue() }
        set(value) {
            runBlocking { appSetting.homeId.setValue(value) }
        }
    private var currentOwnerId
        get() = runBlocking { appSetting.ownerId.getValue() }
        set(value) {
            runBlocking { appSetting.ownerId.setValue(value) }
        }
    private var currentUser: MiotUser? = null
        set(value) {
            field = value
            miotUserClient = value?.let { MiotUserClient(it) }
            miotHomeClient = value?.let { MiotHomeClient(it) }
        }
    override val miotUser: MiotUser? get() = currentUser
    override val homes = MutableResultListStateFlow<MiotHome>(Resultat.Loading())
    override val devices = MutableResultListStateFlow<MiotDevice>(Resultat.Loading())
    override val scenes = MutableResultListStateFlow<MiotScene>(Resultat.Loading())
    override val loginStatus = MutableStateFlow<LoginState>(LoginState.Loading)
    override val userInfo = MutableStateFlow(UserInfo(0L, "", "null"))
    override val currentRoomList: StateFlow<Map<String, MiotDevice>> = TODO()

    init {
        dataStore.data.onEach { user ->
            currentUser = user
            loginStatus.emit(LoginState.Loading)
            val isTokenValid = miotUserClient
                ?.takeIf { user.isLogin() }
                ?.getIsServiceTokenValid()
                ?.getOrNull()
                ?: false
            if (!isTokenValid) {
                loginProvider.refreshServiceToken(user)
                    .onSuccess { newUser -> dataStore.updateData { newUser } }
                    .onFailure { e ->
                        e.printStackTrace()
                        if (e is MiotAuthException || e is MissingFieldException) {
                            // 这里登录信息彻底过期, 需要退出应用再登录
                            loginStatus.emit(LoginState.Failure(e.message ?: "unknown", e))
                        } else {
                            loginStatus.emit(LoginState.NetworkError(e.message ?: "unknown"))
                        }
                    }
            } else {
                refreshUserInfo()
                loginStatus.emit(LoginState.Success)
                refreshAll()
            }
        }.launchIn(scope)

    }

    override fun refreshAll() {
        refreshHomes()
        refreshDevices()
        refreshScenes()
    }

    override fun refreshUserInfo() = scope.launch {
        runCatching {
            getUserInfo().getOrThrow()
        }.onSuccess {
            userInfo.emit(it.info)
        }.onFailure {
            userInfo.emit(UserInfo(0L, "", "null"))
        }
    }

    override fun refreshHomes() = scope.launch {
        homes.emit(Resultat.Loading())
        runCatching {
            val homes = miotHomeClient
                ?.getHomes()
                ?.getOrThrow()
                ?.result
                ?: throw MiotClientException("MiotHomeClient is null")
            buildList {
                addAll(homes.homes)
                homes.shareHomes?.let { addAll(it) }
            }
        }.onSuccess { list ->
            list.takeIf { currentHomeId == 0L }
                ?.firstOrNull()
                ?.let { setActiveHome(it) }
        }.onFailure {

        }.let { homes.emit(it.toResultat()) }
    }

    override fun refreshDevices() = scope.launch {
        devices.emit(Resultat.Loading())
        runCatching {
            val (homeId, ownerId) = getHomeDetails().getOrThrow()
            // miotUser?.let { loginProvider.refreshServiceToken(it) }
            miotHomeClient
                ?.getDevices(homeId, ownerId)
                ?.getOrThrow()
                ?.result
                ?.deviceInfo
                ?: emptyList()
                ?: throw MiotClientException("MiotHomeClient is null")
        }.onSuccess {
            deviceRepository.addIcon(it.map(MiotDevice::model))
        }.onFailure {
        }.let { devices.emit(it.toResultat()) }
    }

    override fun refreshScenes() = scope.launch {
        scenes.emit(Resultat.Loading())
        runCatching {
            val (homeId, ownerUid) = getHomeDetails().getOrThrow()
            miotHomeClient
                ?.getScenes(homeId, ownerUid)
                ?.getOrThrow()
                ?.result
                ?.scenes
                ?: emptyList()
                ?: throw MiotClientException("MiotHomeClient is null")
        }.onFailure {

        }.let { scenes.emit(it.toResultat()) }
    }

    @Throws(IllegalStateException::class)
    fun getHomeDetails(): Result<Pair<Long, Long>> = runCatching {
        val homeId = currentHomeId
        val ownerUid = currentOwnerId
        if (ownerUid == 0L || homeId == 0L)
            throw IllegalStateException("Invalid ownerUid or homeId")
        homeId to ownerUid
    }

    override suspend fun runScene(homeId: Long, ownerUid: Long, scene: MiotScene) =
        runCatching {
            miotHomeClient
                ?.runScene(homeId, ownerUid, scene)
                ?.getOrThrow()
                ?: throw MiotClientException("MiotHomeClient is null")
        }

    override suspend fun getUserInfo() = runCatching {
        miotUserClient
            ?.getUserInfo()
            ?.getOrThrow()
            ?: throw IllegalStateException("MiotUserClient is null")
    }

    override suspend fun setActiveHome(home: MiotHome): Unit =
        withContext(Dispatchers.IO) {
            currentHomeId = home.id.toLong()
            currentOwnerId = home.uid
            refreshDevices()
            refreshScenes()
        }

    @Suppress("FunctionName")
    private fun <T> MutableResultListStateFlow(value: Resultat<List<T>>) = MutableStateFlow(value)
}
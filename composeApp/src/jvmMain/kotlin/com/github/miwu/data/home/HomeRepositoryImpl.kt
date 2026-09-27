package com.github.miwu.data.home

import com.github.miwu.di.AppScope
import com.github.miwu.domain.gateway.MiotClientFactory
import com.github.miwu.domain.model.HomeData
import com.github.miwu.domain.model.LoginState
import com.github.miwu.domain.repository.AccountRepository
import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.domain.repository.HomeRepository
import com.github.miwu.domain.repository.ResultState
import com.github.miwu.domain.repository.SettingsRepository
import fr.haan.resultat.Resultat
import fr.haan.resultat.toResultat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import miwu.miot.client.MiotHomeClient
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.UserInfo
import org.koin.core.annotation.Singleton

@Singleton
class HomeRepositoryImpl(
    private val accountRepository: AccountRepository,
    private val metadataRepository: DeviceMetadataRepository,
    private val settingsRepository: SettingsRepository,
    private val homeDataLoader: HomeDataLoader,
    private val clientFactory: MiotClientFactory,
    @AppScope applicationScope: CoroutineScope,
) : HomeRepository {
    private var homeClient: MiotHomeClient? = null
    private val cachedHomes = mutableMapOf<String, HomeData>()
    private val mutableHomes = MutableStateFlow<Resultat<List<MiotHome>>>(Resultat.loading())
    private val mutableCurrentHome = MutableStateFlow<Resultat<HomeData>>(Resultat.loading())
    private val mutableUserInfo = MutableStateFlow(EMPTY_USER_INFO)

    override val homes: ResultState<List<MiotHome>> = mutableHomes.asStateFlow()
    override val currentHome: ResultState<HomeData> = mutableCurrentHome.asStateFlow()
    override val userInfo = mutableUserInfo.asStateFlow()

    init {
        accountRepository.loginState.onEach(::onLoginStateChanged).launchIn(applicationScope)
    }

    override suspend fun selectHome(home: MiotHome): Result<Unit> {
        mutableCurrentHome.emit(Resultat.loading())
        val result = runCatchingSuspend {
            settingsRepository.selectHome(home.id.toLong(), home.uid)
            cachedHomes[home.id] ?: error("Home ${home.id} is not loaded")
        }
        mutableCurrentHome.emit(result.toResultat())
        return result.map { Unit }
    }

    override suspend fun runScene(scene: MiotScene): Result<Unit> = runCatchingSuspend {
        val client = homeClient ?: error("MiotHomeClient is not initialized")
        val home = homeDataLoader.getSceneHome(scene) ?: error("Scene home is not loaded")
        client.runScene(home, scene).rethrowCancellation().getOrThrow()
    }

    override suspend fun refreshHomes() {
        mutableHomes.emit(Resultat.loading())
        val result = runCatchingSuspend {
            val client = homeClient ?: error("MiotHomeClient is not initialized")
            val response = client.getHomes().rethrowCancellation().getOrThrow().result
            val homeList = response.homes + response.shareHomes.orEmpty()
            cachedHomes.clear()
            cachedHomes.putAll(homeDataLoader.load(client, homeList))
            homeList
        }
        mutableHomes.emit(result.toResultat())

        result.getOrNull()?.let { homeList ->
            val selectedHomeId = settingsRepository.getHomeSelection()?.homeId?.toString()
            val selectedHome = homeList.firstOrNull { it.id == selectedHomeId } ?: homeList.firstOrNull()
            selectedHome?.let { selectHome(it) }
        }
    }

    override suspend fun refreshCurrentHome() {
        mutableCurrentHome.emit(Resultat.loading())
        val result = runCatchingSuspend {
            val client = homeClient ?: error("MiotHomeClient is not initialized")
            val homeId = settingsRepository.getHomeSelection()?.homeId ?: error("No active home")
            val home = cachedHomes[homeId.toString()]?.home ?: error("Active home is not loaded")
            homeDataLoader.load(client, home)
        }
        result.onSuccess { cachedHomes[it.home.id] = it }
        mutableCurrentHome.emit(result.toResultat())
    }

    private suspend fun onLoginStateChanged(state: LoginState) {
        when (state) {
            LoginState.Success -> {
                val user = accountRepository.currentUser ?: return
                homeClient = clientFactory.createHomeClient(user)
                refreshUserInfo(user)
                refreshHomes()
            }

            LoginState.LoggedOut, is LoginState.Failure -> clearAccountState()
            LoginState.Loading, is LoginState.NetworkError -> Unit
        }
    }

    private suspend fun refreshUserInfo(user: MiotUser) {
        runCatchingSuspend {
            clientFactory.createUserClient(user).getUserInfo().rethrowCancellation().getOrThrow().result
        }.onSuccess {
            mutableUserInfo.emit(it)
        }.onFailure {
            mutableUserInfo.emit(EMPTY_USER_INFO)
        }
    }

    private suspend fun clearAccountState() {
        homeClient = null
        cachedHomes.clear()
        homeDataLoader.clear()
        settingsRepository.clearHomeSelection()
        metadataRepository.clear()
        mutableHomes.emit(Resultat.loading())
        mutableCurrentHome.emit(Resultat.loading())
        mutableUserInfo.emit(EMPTY_USER_INFO)
    }

    private suspend inline fun <T> runCatchingSuspend(block: () -> T): Result<T> = try {
        Result.success(block())
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        Result.failure(error)
    }

    private fun <T> Result<T>.rethrowCancellation(): Result<T> =
        onFailure { if (it is CancellationException) throw it }

    private companion object {
        val EMPTY_USER_INFO = UserInfo(-1, "", "")
    }
}

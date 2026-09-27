package com.github.miwu.domain.usecase.device

import com.github.miwu.domain.model.DeviceMetadata
import com.github.miwu.domain.model.HomeData
import com.github.miwu.domain.model.LoginState
import com.github.miwu.domain.repository.AccountRepository
import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.domain.repository.HomeRepository
import fr.haan.resultat.Resultat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonNull
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotRoom
import miwu.miot.model.miot.MiotScene
import miwu.miot.model.miot.UserInfo

class DeviceUseCasesTest {
    private val onlineBedroom = device("online-bedroom", "Beta", true, "model-b")
    private val onlineLivingRoom = device("online-living", "Alpha", true, "model-a")
    private val offlineLivingRoom = device("offline-living", "Gamma", false, "model-c")
    private val homeData = homeData(listOf(offlineLivingRoom, onlineBedroom, onlineLivingRoom))

    @Test
    fun sortedDevicesPutOnlineDevicesFirstThenUseRoomAndName() = runBlocking {
        val homeRepository = FakeHomeRepository(homeData)
        val metadataRepository = FakeMetadataRepository(
            DeviceMetadata(
                rooms = mapOf(
                    onlineBedroom.did to "卧室",
                    onlineLivingRoom.did to "客厅",
                    offlineLivingRoom.did to "客厅",
                )
            )
        )

        val result = GetSortedDevicesUseCase(homeRepository, metadataRepository).invoke().first()

        assertEquals(listOf(onlineBedroom, onlineLivingRoom, offlineLivingRoom), result)
    }

    @Test
    fun resolveDeviceSessionUsesOnlyAValidatedIdAndAuthenticatedUser() {
        val user = user()
        val accountRepository = FakeAccountRepository(user)
        val useCase = ResolveDeviceSessionUseCase(accountRepository, FakeHomeRepository(homeData))

        assertSame(onlineLivingRoom, useCase(onlineLivingRoom.did)?.device)
        assertSame(user, useCase(onlineLivingRoom.did)?.user)
        assertNull(useCase("../../invalid"))
        assertNull(useCase("missing-device"))
    }

    @Test
    fun resolveDeviceSessionRejectsMissingCredentials() {
        val accountRepository = FakeAccountRepository(user().copy(passToken = ""))
        val useCase = ResolveDeviceSessionUseCase(accountRepository, FakeHomeRepository(homeData))

        assertNull(useCase(onlineLivingRoom.did))
    }

    private class FakeAccountRepository(
        override var currentUser: MiotUser?,
    ) : AccountRepository {
        override val loginState = MutableStateFlow<LoginState>(LoginState.Success)

        override suspend fun saveUser(user: MiotUser): MiotUser = user.also { currentUser = it }

        override suspend fun logout() {
            currentUser = null
        }
    }

    private class FakeMetadataRepository(initial: DeviceMetadata) : DeviceMetadataRepository {
        override val metadata = MutableStateFlow(initial)

        override suspend fun ensureIcons(models: Collection<String>) = Unit

        override suspend fun updateRooms(rooms: Map<String, String>) = Unit

        override suspend fun clear() {
            metadata.value = DeviceMetadata()
        }
    }

    private class FakeHomeRepository(homeData: HomeData) : HomeRepository {
        override val userInfo = MutableStateFlow(UserInfo(-1, "", ""))
        override val homes = MutableStateFlow<Resultat<List<MiotHome>>>(Resultat.success(listOf(homeData.home)))
        override val currentHome = MutableStateFlow<Resultat<HomeData>>(Resultat.success(homeData))

        override suspend fun selectHome(home: MiotHome) = Result.success(Unit)

        override suspend fun runScene(scene: MiotScene) = Result.success(Unit)

        override suspend fun refreshHomes() = Unit

        override suspend fun refreshCurrentHome() = Unit
    }

    private companion object {
        fun user() = MiotUser("1", "2", 3, "security", "psecurity", "token", "service", "device")

        fun device(did: String, name: String, online: Boolean, model: String) = MiotDevice(
            bssid = "",
            comFlag = 0,
            did = did,
            extra = MiotDevice.Extra(),
            freqFlag = false,
            isOnline = online,
            latitude = "",
            longitude = "",
            mac = "",
            model = model,
            name = name,
            orderTime = 0,
            permitLevel = 0,
            pid = 0,
            specType = "urn:miot-spec-v2:device:light:0000A001:test:1",
            token = "",
            uid = 1,
        )

        fun homeData(devices: List<MiotDevice>): HomeData {
            val room = MiotRoom("", "", 0, devices.map(MiotDevice::did), "", 1, "客厅", "1", 0)
            val home = MiotHome(
                address = "",
                background = "",
                bssid = "",
                carDid = "",
                cityId = 0,
                createTime = 0,
                dids = devices.map(MiotDevice::did),
                icon = "",
                id = "1",
                latitude = 0.0,
                longitude = 0.0,
                name = "Home",
                permitLevel = 0,
                popupFlag = 0,
                popupTimeStamp = 0,
                rooms = listOf(room),
                shareFlag = 0,
                smartRoomBackground = "",
                status = 0,
                tempDids = JsonNull,
                uid = 1,
            )
            return HomeData(home, mapOf(room.name to devices), emptyList(), devices, mapOf(room.name to room))
        }
    }
}

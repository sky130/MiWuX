package com.github.miwu.data.account

import com.github.miwu.data.account.local.MiotUserDataStore
import com.github.miwu.data.account.local.serializer.MiotUserSerializer
import com.github.miwu.di.AppScope
import com.github.miwu.domain.gateway.MiotClientFactory
import com.github.miwu.domain.model.LoginState
import com.github.miwu.domain.repository.AccountRepository
import com.github.miwu.domain.repository.SettingsRepository
import com.github.miwu.logging.Logger
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import miwu.miot.exception.MiotAuthException
import miwu.miot.model.MiotUser
import miwu.miot.provider.MiotLoginProvider
import org.koin.core.annotation.Singleton

@Singleton
@OptIn(ExperimentalSerializationApi::class)
class AccountRepositoryImpl(
    private val loginProvider: MiotLoginProvider,
    private val dataStore: MiotUserDataStore,
    private val settingsRepository: SettingsRepository,
    private val clientFactory: MiotClientFactory,
    @AppScope applicationScope: CoroutineScope,
) : AccountRepository {
    private val logger = Logger()
    private val userReference = AtomicReference<MiotUser?>(null)
    private val mutableLoginState = MutableStateFlow<LoginState>(LoginState.Loading)

    override val currentUser: MiotUser?
        get() = userReference.get()

    override val loginState: StateFlow<LoginState> = mutableLoginState.asStateFlow()

    init {
        dataStore.data
            .onEach(::validateStoredUser)
            .catch { error ->
                if (error is CancellationException) throw error
                logger.error("Reading stored credentials failed", error)
                userReference.set(null)
                mutableLoginState.emit(LoginState.Failure(error.message ?: "unknown", error))
            }
            .launchIn(applicationScope)
    }

    override suspend fun saveUser(user: MiotUser): MiotUser {
        try {
            val storedUser = user.copy(deviceId = settingsRepository.getOrCreateDeviceId())
            dataStore.updateData { storedUser }
            userReference.set(storedUser)
            mutableLoginState.emit(LoginState.Success)
            logger.info("Credentials stored")
            return storedUser
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            logger.error("Storing credentials failed", error)
            throw error
        }
    }

    override suspend fun logout() {
        try {
            dataStore.updateData { MiotUserSerializer.defaultValue }
            logger.info("Credentials cleared")
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            logger.error("Clearing credentials failed", error)
            throw error
        }
    }

    private suspend fun validateStoredUser(storedUser: MiotUser) {
        mutableLoginState.emit(LoginState.Loading)
        val user = storedUser.copy(deviceId = settingsRepository.getOrCreateDeviceId())
        if (!user.hasCredentials()) {
            logger.info("Stored credentials are unavailable")
            userReference.set(null)
            mutableLoginState.emit(LoginState.LoggedOut)
            return
        }

        userReference.set(user)
        val validationResult = clientFactory.createUserClient(user)
            .getIsServiceTokenValid()
            .rethrowCancellation()
        validationResult.onFailure { logger.warn("Stored credential validation failed", it) }
        val tokenValid = validationResult.getOrNull() ?: false
        logger.info("Stored credential validation completed; valid={}", tokenValid)
        if (tokenValid) {
            mutableLoginState.emit(LoginState.Success)
        } else {
            refreshToken(user)
        }
    }

    private suspend fun refreshToken(user: MiotUser) {
        logger.info("Refreshing credentials")
        loginProvider.refreshServiceToken(user)
            .rethrowCancellation()
            .onSuccess {
                logger.info("Credential refresh succeeded")
                saveUser(it)
            }
            .onFailure { error ->
                logger.error("Credential refresh failed", error)
                mutableLoginState.emit(error.toLoginState())
            }
    }

    private fun Throwable.toLoginState(): LoginState =
        if (this is MiotAuthException || this is MissingFieldException) {
            LoginState.Failure(message ?: "unknown", this)
        } else {
            LoginState.NetworkError(message ?: "unknown")
        }

    private fun <T> Result<T>.rethrowCancellation(): Result<T> =
        onFailure { if (it is CancellationException) throw it }

    private fun MiotUser.hasCredentials(): Boolean =
        userId.isNotEmpty() && cUserId.isNotEmpty() && passToken.isNotEmpty()
}

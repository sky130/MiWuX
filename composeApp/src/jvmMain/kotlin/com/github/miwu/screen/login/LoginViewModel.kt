package com.github.miwu.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.domain.usecase.account.LoginUseCase
import com.github.miwu.logging.Logger
import fr.haan.resultat.Resultat
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import miwu.miot.model.MiotUser
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    private val logger = Logger()
    private val mutableEvent = MutableSharedFlow<Event>()
    private val mutableQrCode = MutableStateFlow<Resultat<String>>(Resultat.loading())
    private var loginJob: Job? = null

    val event = mutableEvent.asSharedFlow()
    val qrCode = mutableQrCode.asStateFlow()

    fun loadQrCode() {
        logger.info("Starting QR code login")
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            while (true) {
                currentCoroutineContext().ensureActive()
                mutableQrCode.value = Resultat.loading()
                try {
                    val qrCode = loginUseCase.generateQrCode().getOrThrow()
                    logger.info("QR code loaded; waiting for authentication")
                    mutableQrCode.value = Resultat.success(qrCode.data)
                    val user = loginUseCase.loginByQrCode(qrCode.loginUrl).getOrThrow()
                    logger.info("QR code login succeeded")
                    mutableEvent.emit(Event.LoginSuccess(user))
                    return@launch
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Throwable) {
                    if (error.isTimeout()) {
                        logger.info("QR code login timed out; retrying")
                        continue
                    }
                    logger.error("QR code login failed", error)
                    mutableQrCode.value = Resultat.failure(error)
                    mutableEvent.emit(Event.LoginFailure(error))
                    return@launch
                }
            }
        }
    }

    private fun Throwable.isTimeout(): Boolean =
        this is SocketTimeoutException ||
            this is TimeoutException ||
            this is io.ktor.client.network.sockets.SocketTimeoutException

    sealed interface Event {
        data class LoginSuccess(val user: MiotUser) : Event
        data class LoginFailure(val error: Throwable) : Event
    }
}

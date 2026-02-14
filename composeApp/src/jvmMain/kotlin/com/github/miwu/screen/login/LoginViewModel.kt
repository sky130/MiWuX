package com.github.miwu.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.setting.AppSetting
import fr.haan.resultat.Resultat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import miwu.miot.model.MiotUser
import miwu.miot.provider.MiotLoginProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class LoginViewModel() : ViewModel(), KoinComponent {
    private val _event = MutableSharedFlow<Event>()
    private val appSetting: AppSetting by inject()
    private val loginProvider: MiotLoginProvider by inject()
    private val miotUserDataStore: MiotUserDataStore by inject()
    val loginJob = Job()
    val scope = CoroutineScope(loginJob)
    var qrcode by mutableStateOf(Resultat.loading<String>())
        private set
    var miotUser by mutableStateOf<MiotUser?>(null)
        private set

    val event = _event.asSharedFlow()

    fun loadQrCode() {
        loginJob.cancelChildren()
        scope.launch(Dispatchers.IO) {
            runCatching {
                qrcode = Resultat.loading()
                val response = loginProvider
                    .generateLoginQrCode()
                    .getOrThrow()
                val qr = response.toQrCode()
                    ?: error("generate login qrcode failure, response=${response}")
                qrcode = Resultat.success(qr.data)
                loginProvider
                    .loginByQrCode(qr.loginUrl)
                    .getOrThrow()
            }.onFailure { e ->
                e.printStackTrace()
                if (e is SocketTimeoutException || e is TimeoutException) {
                    loadQrCode()
                } else {
                    loginFailure(e)
                }
            }.onSuccess { user ->
                loginSuccess(user)
            }
        }
    }

    private suspend fun loginSuccess(user: MiotUser) {
        val user = user.copy(deviceId = appSetting.deviceId.getValue())
        miotUserDataStore.updateData { user }
        event(Event.LoginSuccess(user))
    }

    private suspend fun loginFailure(e: Throwable) {
        event(Event.LoginFailure(e))
    }

    private suspend fun event(event: Event) = _event.emit(event)

    sealed interface Event {
        data class LoginSuccess(val user: MiotUser) : Event
        data class LoginFailure(val e: Throwable) : Event
        data class ShowLoading(val show: Boolean) : Event
    }
}
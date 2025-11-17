package com.github.miwu.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import fr.haan.resultat.Resultat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import miwu.miot.MiotManager
import miwu.miot.exception.MiotConnectionException
import miwu.miot.exception.MiotHttpException
import miwu.miot.exception.MiotTimeoutException
import miwu.miot.model.MiotUser
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel() : ViewModel(), KoinComponent {
    val miotManager: MiotManager by inject()
    val loginJob = Job()
    val scope = CoroutineScope(loginJob)
    var qrcode by mutableStateOf(Resultat.loading<String>())
        private set
    var miotUser by mutableStateOf<MiotUser?>(null)
        private set

    fun loadQrCode() {
        loginJob.cancelChildren()
        scope.launch {
            runCatching {
                with(miotManager.Login) {
                    val qrCode = generateLoginQrCode().toQrCode()
                    qrcode = Resultat.success(qrCode.data)
                    loginByQrCode(qrCode.loginUrl).getOrThrow()
                }
            }.onFailure { e ->
                when (e) {
                    is MiotTimeoutException -> {
                        return@launch loadQrCode()
                    }

                    is MiotConnectionException -> {

                    }

                    is MiotHttpException -> {

                    }
                }
            }.onSuccess {
                miotUser = it
            }
        }
    }
}
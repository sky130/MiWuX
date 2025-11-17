package com.github.miwu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.miwu.logic.setting.AppSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import miwu.miot.MiotClient
import miwu.miot.MiotManager
import miwu.miot.model.MiotUser
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppViewModel() : ViewModel(), KoinComponent {

    val miotManager: MiotManager by inject()
    val appSetting: AppSetting by inject()
    val miotClient: MiotClient by inject()
    private val settingJob = Job()
    private val settingScope = CoroutineScope(settingJob)
    var miotUser by mutableStateOf<MiotUser?>(null)
        private set
    var isTokenExpired = false
        private set

    fun updateMiotUser(miotUser: MiotUser) {
        settingScope.launch {
            appSetting.userId.setValue(miotUser.userId)
            appSetting.serviceToken.setValue(miotUser.serviceToken)
            appSetting.securityToken.setValue(miotUser.securityToken)
            miotClient.setUser(miotUser)
            isTokenExpired =
                if (miotUser.userId.isBlank()) false
                else miotClient.checkTokenValid().getOrNull() ?: true
            this@AppViewModel.miotUser = miotUser
        }
    }
}
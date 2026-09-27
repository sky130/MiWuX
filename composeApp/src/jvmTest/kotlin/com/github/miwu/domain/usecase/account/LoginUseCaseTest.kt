package com.github.miwu.domain.usecase.account

import com.github.miwu.domain.model.LoginState
import com.github.miwu.domain.repository.AccountRepository
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import miwu.miot.model.MiotUser
import miwu.miot.model.login.LoginQrCode
import miwu.miot.provider.MiotLoginProvider

class LoginUseCaseTest {
    private val authenticatedUser = MiotUser(
        "1",
        "2",
        3,
        "security",
        "psecurity",
        "token",
        "service",
        "device",
    )

    @Test
    fun generatesQrCodeData() = runBlocking {
        val useCase = LoginUseCase(FakeLoginProvider(authenticatedUser), FakeAccountRepository(), Dispatchers.Unconfined)

        val qrCode = useCase.generateQrCode().getOrThrow()

        assertEquals("qr-data", qrCode.data)
        assertEquals("login-url", qrCode.loginUrl)
    }

    @Test
    fun savesUserAfterQrCodeLogin() = runBlocking {
        val accountRepository = FakeAccountRepository()
        val useCase = LoginUseCase(FakeLoginProvider(authenticatedUser), accountRepository, Dispatchers.Unconfined)

        val result = useCase.loginByQrCode("login-url").getOrThrow()

        assertSame(authenticatedUser, result)
        assertSame(authenticatedUser, accountRepository.currentUser)
    }

    private class FakeAccountRepository : AccountRepository {
        override var currentUser: MiotUser? = null
        override val loginState = MutableStateFlow<LoginState>(LoginState.LoggedOut)

        override suspend fun saveUser(user: MiotUser): MiotUser = user.also { currentUser = it }

        override suspend fun logout() {
            currentUser = null
        }
    }

    private class FakeLoginProvider(
        private val user: MiotUser,
    ) : MiotLoginProvider {
        override suspend fun login(user: String, pwd: String) = Result.success(this.user)

        override suspend fun loginByQrCode(loginUrl: String) = Result.success(user)

        override suspend fun loginByQrCode(
            loginUrl: String,
            onSuccess: suspend CoroutineScope.(MiotUser) -> Unit,
            onTimeout: suspend CoroutineScope.() -> Unit,
            onFailure: suspend CoroutineScope.(Throwable?) -> Unit,
            context: CoroutineContext,
        ) {
            CoroutineScope(context).onSuccess(user)
        }

        override suspend fun generateLoginQrCode() = Result.success(
            LoginQrCode(
                code = 0,
                desc = "",
                description = "",
                loginUrl = "qr-data",
                lp = "login-url",
                result = "ok",
            )
        )

        override suspend fun refreshServiceToken(miotUser: MiotUser) = Result.success(miotUser)
    }
}

package com.github.miwu.logic.repository

import com.github.miwu.logic.repository.entity.MiotHomeData
import com.github.miwu.logic.state.LoginState
import fr.haan.resultat.Resultat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome

typealias ResultatState<T> = StateFlow<Resultat<T>>
typealias MutableResultatState<T> = MutableStateFlow<Resultat<T>>

interface MiotRepository {
    val loginStatus: StateFlow<LoginState>

    val homes: ResultatState<List<MiotHome>>

    val currentHome: ResultatState<MiotHomeData>

    fun setActiveHome(home: MiotHome)

    fun refreshHomes()

    fun refreshCurrentHome()
}
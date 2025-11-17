package com.github.miwu.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import fr.haan.resultat.Resultat
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

inline fun <reified T> simpleDataFlow(crossinline block: suspend () -> T) = flow {
    emit(Resultat.loading())
    emit(Resultat.success(block()))
}.catch { emit(Resultat.failure(it)) }

@Composable
inline fun <reified T> rememberSimpleDataFlow(crossinline block: suspend () -> T) = remember {
    simpleDataFlow(block)
}
package ru.easycode.zerotoheroandroidtdd.ui.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal interface RunAsync {
    fun <T : Any> runFlowCollect(
        scope: CoroutineScope,
        flow: Flow<T>,
        collect: suspend (T) -> Unit
    )
}

internal class DefaultRunAsync : RunAsync {
    override fun <T : Any> runFlowCollect(
        scope: CoroutineScope,
        flow: Flow<T>,
        collect: suspend (T) -> Unit
    ) {
        scope.launch { flow.collect { collect(it) } }
    }
}
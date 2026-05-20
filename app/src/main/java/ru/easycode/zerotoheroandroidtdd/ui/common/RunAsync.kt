package ru.easycode.zerotoheroandroidtdd.ui.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

internal interface RunAsync {
    fun <T : Any> runFlowCollect(
        scope: CoroutineScope,
        flow: Flow<T>,
        collect: suspend (T) -> Unit
    )
}
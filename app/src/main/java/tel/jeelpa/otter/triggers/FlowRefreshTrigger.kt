package tel.jeelpa.otter.triggers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart

class RefreshableFlow {
    private val hotFlow = MutableSharedFlow<Unit>()

    suspend fun refresh() =
        hotFlow.emit(Unit)


    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun <T : Any> invoke(flowSource: Flow<T>): Flow<T> {
        return hotFlow
            .onStart { emit(Unit) }
            .flatMapLatest { flowSource }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T : Any> withCondition(flowSource: Flow<T>, condition: suspend () -> Boolean): Flow<T> {
        val firstCollect = TrueOnlyOnce()

        return hotFlow
            .onStart { emit(Unit) }
            .filter { condition() || firstCollect() }
            .flatMapLatest { flowSource }
    }

}

class TrueOnlyOnce {
    private var first = true
    operator fun invoke(): Boolean {
        if (first) {
            first = false
            return true
        }
        return false
    }
}

fun <T : Any> Flow<T>.refreshOn(refreshableFlow: RefreshableFlow) : Flow<T> {
    return refreshableFlow(this)
}

fun <T : Any> Flow<T>.refreshConditionallyOn(refreshableFlow: RefreshableFlow, condition: suspend () -> Boolean) : Flow<T> {
    return refreshableFlow.withCondition(this, condition)
}

class RefreshTrigger(
    private val refreshTrigger: RefreshableFlow
) {
    suspend operator fun invoke() = refreshTrigger.refresh()
}

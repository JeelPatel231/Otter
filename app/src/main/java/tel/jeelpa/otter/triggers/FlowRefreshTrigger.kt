package tel.jeelpa.otter.triggers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
}

fun <T : Any> Flow<T>.refreshOn(refreshableFlow: RefreshableFlow) : Flow<T> {
    return refreshableFlow(this)
}

class RefreshTrigger(
    private val refreshTrigger: RefreshableFlow
) {
    suspend operator fun invoke() = refreshTrigger.refresh()
}

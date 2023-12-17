package tel.jeelpa.otter.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.triggers.RefreshableFlow
import tel.jeelpa.otter.triggers.refreshOn
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import tel.jeelpa.plugininterface.tracker.repository.UserClient
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userClient: UserClient,
    @Named("UserDataRefreshFlow") refreshableFlow: RefreshableFlow,
): ViewModel() {

    val userData = suspendToFlow { userClient.getUser() }
        .cacheInScope(viewModelScope)

    val currentAnime = userClient.getCurrentAnime()
        .refreshOn(refreshableFlow)
        .cacheInScope(viewModelScope)

    val currentManga = userClient.getCurrentManga()
        .refreshOn(refreshableFlow)
        .cacheInScope(viewModelScope)

    val recommendations = userClient.getRecommendations()
        .cacheInScope(viewModelScope)
}

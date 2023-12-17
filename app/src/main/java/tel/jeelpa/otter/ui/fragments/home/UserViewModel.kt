package tel.jeelpa.otter.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import tel.jeelpa.otter.models.SettingsStore
import tel.jeelpa.otter.triggers.RefreshableFlow
import tel.jeelpa.otter.triggers.refreshConditionallyOn
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import tel.jeelpa.plugininterface.tracker.repository.UserClient
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userClient: UserClient,
    private val settingsStore: SettingsStore,
    @Named("UserDataRefreshFlow") userDataRefreshableFlow: RefreshableFlow,
): ViewModel() {

    private suspend fun liveRefreshSetting() =
        settingsStore.liveRefresh.get().first()

    val userData = suspendToFlow { userClient.getUser() }
        .cacheInScope(viewModelScope)

    val currentAnime = userClient.getCurrentAnime()
        .refreshConditionallyOn(userDataRefreshableFlow) { liveRefreshSetting() }
        .cacheInScope(viewModelScope)

    val currentManga = userClient.getCurrentManga()
        .refreshConditionallyOn(userDataRefreshableFlow) { liveRefreshSetting() }
        .cacheInScope(viewModelScope)

    val recommendations = userClient.getRecommendations()
        .cacheInScope(viewModelScope)
}

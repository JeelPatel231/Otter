package tel.jeelpa.otter.ui.fragments.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import tel.jeelpa.otter.models.SettingsStore
import tel.jeelpa.otter.triggers.RefreshableFlow
import tel.jeelpa.otter.triggers.refreshConditionallyOn
import tel.jeelpa.plugininterface.tracker.repository.AnimeClient
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AnimeFragmentViewModel @Inject constructor(
    private val animeClient: AnimeClient,
    private val settingsStore: SettingsStore,
    @Named("UserDataRefreshFlow") private val userDataRefreshableFlow: RefreshableFlow
): ViewModel() {

    private suspend fun liveRefreshSetting() =
        settingsStore.liveRefresh.get().first()


    val trendingAnime = animeClient.getTrendingAnime()
        .refreshConditionallyOn(userDataRefreshableFlow) { liveRefreshSetting() }
        .cachedIn(viewModelScope)

    val recentlyUpdated = animeClient.getRecentlyUpdated()
        .refreshConditionallyOn(userDataRefreshableFlow) { liveRefreshSetting() }
        .cachedIn(viewModelScope)

    val popularAnime = animeClient.getPopularAnime()
        .refreshConditionallyOn(userDataRefreshableFlow) { liveRefreshSetting() }
        .cachedIn(viewModelScope)

    fun searchResults(query: String) = animeClient.search(query).cachedIn(viewModelScope)
}
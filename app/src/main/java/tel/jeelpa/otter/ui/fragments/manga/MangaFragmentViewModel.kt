package tel.jeelpa.otter.ui.fragments.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import tel.jeelpa.otter.models.SettingsStore
import tel.jeelpa.otter.triggers.RefreshableFlow
import tel.jeelpa.otter.triggers.refreshConditionallyOn
import tel.jeelpa.plugininterface.tracker.repository.MangaClient
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MangaFragmentViewModel @Inject constructor(
    private val mangaClient: MangaClient,
    private val settingsStore: SettingsStore,
    @Named("UserDataRefreshFlow") private val userDataRefreshableFlow: RefreshableFlow
): ViewModel() {

    private suspend fun liveRefreshSetting() =
        settingsStore.liveRefresh.get().first()

    val trendingManga = mangaClient.getTrendingManga()
        .refreshConditionallyOn(userDataRefreshableFlow) { liveRefreshSetting() }
        .cachedIn(viewModelScope)

    val popularManga = mangaClient.getPopularManga()
        .refreshConditionallyOn(userDataRefreshableFlow) { liveRefreshSetting() }
        .cachedIn(viewModelScope)

    val trendingNovel = mangaClient.getTrendingNovel()
        .refreshConditionallyOn(userDataRefreshableFlow) { liveRefreshSetting() }
        .cachedIn(viewModelScope)

    fun searchResults(query: String) = mangaClient.search(query).cachedIn(viewModelScope)
}
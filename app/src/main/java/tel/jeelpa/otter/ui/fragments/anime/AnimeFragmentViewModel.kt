package tel.jeelpa.otter.ui.fragments.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.triggers.RefreshableFlow
import tel.jeelpa.otter.triggers.refreshOn
import tel.jeelpa.plugininterface.tracker.repository.AnimeClient
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AnimeFragmentViewModel @Inject constructor(
    private val animeClient: AnimeClient,
    @Named("UserDataRefreshFlow") private val userDataRefreshableFlow: RefreshableFlow
): ViewModel() {

    val trendingAnime = animeClient.getTrendingAnime()
        .refreshOn(userDataRefreshableFlow)
        .cachedIn(viewModelScope)

    val recentlyUpdated = animeClient.getRecentlyUpdated()
        .refreshOn(userDataRefreshableFlow)
        .cachedIn(viewModelScope)

    val popularAnime = animeClient.getPopularAnime()
        .refreshOn(userDataRefreshableFlow)
        .cachedIn(viewModelScope)

    fun searchResults(query: String) = animeClient.search(query).cachedIn(viewModelScope)
}
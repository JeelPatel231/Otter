package tel.jeelpa.otter.ui.fragments.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.triggers.RefreshableFlow
import tel.jeelpa.otter.triggers.refreshOn
import tel.jeelpa.plugininterface.tracker.repository.MangaClient
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MangaFragmentViewModel @Inject constructor(
    private val mangaClient: MangaClient,
    @Named("UserDataRefreshFlow") private val userDataRefreshableFlow: RefreshableFlow
): ViewModel() {

    val trendingManga = mangaClient.getTrendingManga()
        .refreshOn(userDataRefreshableFlow)
        .cachedIn(viewModelScope)

    val popularManga = mangaClient.getPopularManga()
        .refreshOn(userDataRefreshableFlow)
        .cachedIn(viewModelScope)

    val trendingNovel = mangaClient.getTrendingNovel()
        .refreshOn(userDataRefreshableFlow)
        .cachedIn(viewModelScope)

    fun searchResults(query: String) = mangaClient.search(query).cachedIn(viewModelScope)
}
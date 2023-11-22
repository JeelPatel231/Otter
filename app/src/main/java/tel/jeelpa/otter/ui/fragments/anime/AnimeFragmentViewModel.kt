package tel.jeelpa.otter.ui.fragments.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.plugininterface.tracker.repository.AnimeClient
import javax.inject.Inject

@HiltViewModel
class AnimeFragmentViewModel @Inject constructor(
    private val animeClient: AnimeClient
): ViewModel() {

    val trendingAnime = animeClient.getTrendingAnime().cachedIn(viewModelScope)

    val recentlyUpdated = animeClient.getRecentlyUpdated().cachedIn(viewModelScope)

    val popularAnime = animeClient.getPopularAnime().cachedIn(viewModelScope)

    fun searchResults(query: String) = animeClient.search(query).cachedIn(viewModelScope)
}
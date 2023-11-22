package tel.jeelpa.otter.ui.fragments.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.plugininterface.tracker.repository.MangaClient
import javax.inject.Inject

@HiltViewModel
class MangaFragmentViewModel @Inject constructor(
    private val mangaClient: MangaClient
): ViewModel() {

    val trendingManga = mangaClient.getTrendingManga().cachedIn(viewModelScope)

    val popularManga = mangaClient.getPopularManga().cachedIn(viewModelScope)

    val trendingNovel = mangaClient.getTrendingNovel().cachedIn(viewModelScope)

    fun searchResults(query: String) = mangaClient.search(query).cachedIn(viewModelScope)
}
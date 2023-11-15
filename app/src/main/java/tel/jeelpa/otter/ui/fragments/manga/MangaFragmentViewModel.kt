package tel.jeelpa.otter.ui.fragments.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tel.jeelpa.otter.trackerinterface.models.MediaCardData
import tel.jeelpa.otter.trackerinterface.repository.MangaClient
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import javax.inject.Inject

@HiltViewModel
class MangaFragmentViewModel @Inject constructor(
    private val mangaClient: MangaClient
): ViewModel() {

    val trendingManga = suspendToFlow { mangaClient.getTrendingManga() }
        .cacheInScope(viewModelScope)

    val popularManga = suspendToFlow { mangaClient.getPopularManga() }
        .cacheInScope(viewModelScope)

    val trendingNovel = suspendToFlow { mangaClient.getTrendingNovel() }
        .cacheInScope(viewModelScope)

    private val _searchResults: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val searchResults = _searchResults.asStateFlow()

    fun search(query: String) = viewModelScope.launch {
        _searchResults.value = mangaClient.search(query)
    }

}
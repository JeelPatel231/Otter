package tel.jeelpa.otter.ui.fragments.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.repository.MangaClient
import javax.inject.Inject

@HiltViewModel
class MangaFragmentViewModel @Inject constructor(
    private val mangaClient: MangaClient
): ViewModel() {
    private val _trendingManga: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val trendingManga = _trendingManga.asStateFlow()

    private val _popularManga: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val popularManga = _popularManga.asStateFlow()

    private val _trendingNovel: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val trendingNovel = _trendingNovel.asStateFlow()

    private val _searchResults: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val searchResults = _searchResults.asStateFlow()

    fun search(query: String) = viewModelScope.launch {
        _searchResults.value = mangaClient.search(query)
    }
    init {
        with(viewModelScope) {
            launch { _trendingManga.value = mangaClient.getTrendingManga() }
            launch { _popularManga.value = mangaClient.getPopularManga() }
            launch { _trendingNovel.value = mangaClient.getTrendingNovel() }
        }
    }
}
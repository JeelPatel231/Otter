package tel.jeelpa.otter.ui.fragments.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.repository.AnimeClient
import javax.inject.Inject

@HiltViewModel
class AnimeFragmentViewModel @Inject constructor(
    private val animeClient: AnimeClient
): ViewModel() {
    private val _trendingAnime: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val trendingAnime = _trendingAnime.asStateFlow()

    private val _popularAnime: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val popularAnime = _popularAnime.asStateFlow()

    private val _recentlyUpdated: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val recentlyUpdated = _recentlyUpdated.asStateFlow()

    private val _searchResults: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val searchResults = _searchResults.asStateFlow()

    fun search(query: String) = viewModelScope.launch {
        _searchResults.value = animeClient.search(query)
    }

    init {
        with(viewModelScope) {
            launch { _trendingAnime.value = animeClient.getTrendingAnime() }
            launch { _popularAnime.value = animeClient.getPopularAnime() }
            launch { _recentlyUpdated.value = animeClient.getRecentlyUpdated() }
        }
    }

}
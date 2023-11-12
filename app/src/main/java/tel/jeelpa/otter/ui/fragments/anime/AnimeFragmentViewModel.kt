package tel.jeelpa.otter.ui.fragments.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.repository.AnimeClient
import javax.inject.Inject

@HiltViewModel
class AnimeFragmentViewModel @Inject constructor(
    private val animeClient: AnimeClient
): ViewModel() {
    val trendingAnime = suspendToFlow { animeClient.getTrendingAnime() }
        .cacheInScope(viewModelScope)

    val popularAnime = suspendToFlow { animeClient.getPopularAnime() }
        .cacheInScope(viewModelScope)

    val recentlyUpdated = suspendToFlow { animeClient.getRecentlyUpdated() }
        .cacheInScope(viewModelScope)

    private val _searchResults: MutableStateFlow<List<MediaCardData>?> = MutableStateFlow(null)
    val searchResults = _searchResults.asStateFlow()

    fun search(query: String) = viewModelScope.launch {
        _searchResults.value = animeClient.search(query)
    }

}
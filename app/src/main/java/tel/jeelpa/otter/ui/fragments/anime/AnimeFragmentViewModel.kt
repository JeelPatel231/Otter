package tel.jeelpa.otter.ui.fragments.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.ui.generic.GenericPagingSource
import tel.jeelpa.plugininterface.tracker.repository.AnimeClient
import javax.inject.Inject

@HiltViewModel
class AnimeFragmentViewModel @Inject constructor(
    private val animeClient: AnimeClient
): ViewModel() {

    val trendingAnime = Pager(
        config = PagingConfig(
            30,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { GenericPagingSource(animeClient::getTrendingAnime) }
    ).flow.cachedIn(viewModelScope)


    val recentlyUpdated = Pager(
        config = PagingConfig(
            30,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { GenericPagingSource(animeClient::getRecentlyUpdated) }
    ).flow.cachedIn(viewModelScope)


    val popularAnime = Pager(
        config = PagingConfig(
            30,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { GenericPagingSource(animeClient::getPopularAnime) }
    ).flow.cachedIn(viewModelScope)

    fun searchResults(query: String) = Pager(
        config = PagingConfig(
            pageSize = 30,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { GenericPagingSource( { page, perPage -> animeClient.search(query, page, perPage)} ) }
    ).flow.cachedIn(viewModelScope)

}
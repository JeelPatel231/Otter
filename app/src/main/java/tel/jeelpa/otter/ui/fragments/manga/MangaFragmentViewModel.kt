package tel.jeelpa.otter.ui.fragments.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.ui.generic.GenericPagingSource
import tel.jeelpa.plugininterface.tracker.repository.MangaClient
import javax.inject.Inject

@HiltViewModel
class MangaFragmentViewModel @Inject constructor(
    private val mangaClient: MangaClient
): ViewModel() {

    val trendingManga = Pager(
        config = PagingConfig(
            30,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { GenericPagingSource(mangaClient::getTrendingManga) }
    ).flow.cachedIn(viewModelScope)


    val popularManga = Pager(
        config = PagingConfig(
            30,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { GenericPagingSource(mangaClient::getPopularManga) }
    ).flow.cachedIn(viewModelScope)

    val trendingNovel = Pager(
        config = PagingConfig(
            30,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { GenericPagingSource(mangaClient::getTrendingNovel) }
    ).flow.cachedIn(viewModelScope)


    fun searchResults(query: String) = Pager(
        config = PagingConfig(
            pageSize = 30,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { GenericPagingSource( { page, perPage -> mangaClient.search(query, page, perPage)} ) }
    ).flow.cachedIn(viewModelScope)

}
package tel.jeelpa.otter.ui.fragments.animedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import tel.jeelpa.otter.reference.Parser
import tel.jeelpa.otter.reference.ParserManager
import tel.jeelpa.otter.reference.models.Episode
import tel.jeelpa.otter.reference.models.ShowResponse
import tel.jeelpa.otter.reference.models.VideoServer
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.repository.AnimeClient
import javax.inject.Inject


@HiltViewModel
class AnimeDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animeClient: AnimeClient,
    private val parserManager: ParserManager,
) : ViewModel() {
    val navArgs = savedStateHandle.get<MediaCardData>("data")!!

    val animeDetails = suspendToFlow { animeClient.getAnimeDetails(navArgs.id) }
        .cacheInScope(viewModelScope)

    val mediaOpenings = channelFlow { animeDetails.collectLatest {
            it.idMal?.let { idMal -> send(animeClient.getOpenings(idMal)) }
    } }.cacheInScope(viewModelScope)

    val mediaEndings = channelFlow { animeDetails.collectLatest {
        it.idMal?.let { idMal -> send(animeClient.getEndings(idMal)) }
    } }.cacheInScope(viewModelScope)

    val parsers
        get() = parserManager.parsers

    // Watch Fragment Data
    private val _selectedParser: MutableStateFlow<Parser?> = MutableStateFlow(null)
    private val _searchedAnimes = MutableStateFlow(emptyList<ShowResponse>())
    private val _selectedAnime: MutableStateFlow<ShowResponse?> = MutableStateFlow(null)
    private val _episodesScraped = MutableStateFlow(emptyList<Episode>())

    val selectedParser
        get() = _selectedParser.asStateFlow()
    val searchedAnimes
        get() = _searchedAnimes.asStateFlow()
    val selectedAnime
        get() = _selectedAnime.asStateFlow()
    val episodesScraped
        get() = _episodesScraped.asStateFlow()

    // TODO: Refactor these scraping functions to improve reactivity

    // onParserChange -> clear everything
    // onAnimeChange -> clear anime and episodes

    suspend fun onParserChange(parser: Parser) = withContext(Dispatchers.IO) {
        _selectedParser.value = parser
        _episodesScraped.value = emptyList() // don't wait for searching, clear episodes asap
        searchAnime(navArgs.title)
        val firstAnime = searchedAnimes.value.firstOrNull()
            ?: throw Exception("No Anime Found")
        onSelectAnime(firstAnime)
    }

    suspend fun searchAnime(query: String) = withContext(Dispatchers.IO){
        _searchedAnimes.value = emptyList()
        _searchedAnimes.value = selectedParser.value!!.search(query)
    }
    suspend fun onSelectAnime(anime: ShowResponse){
        _episodesScraped.value = emptyList()
        _selectedAnime.value = anime
        loadEpisodes()
    }

    suspend fun getVideoServers(episodeLink: String): List<VideoServer> = withContext(Dispatchers.IO){
        return@withContext selectedParser.value!!.loadVideoServers(episodeLink)
    }

    private suspend fun loadEpisodes() = withContext(Dispatchers.IO){
        _episodesScraped.value = selectedParser.value!!.loadEpisodes(selectedAnime.value!!.link)
    }

}

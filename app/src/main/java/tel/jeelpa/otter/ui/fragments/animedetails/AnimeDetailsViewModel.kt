package tel.jeelpa.otter.ui.fragments.animedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tel.jeelpa.aniyomi_runtime.AniyomiPluginMeta
import tel.jeelpa.aniyomi_runtime.AniyomiPluginRepo
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import tel.jeelpa.plugger.utils.mapState
import tel.jeelpa.plugininterface.anime.parser.Parser
import tel.jeelpa.plugininterface.models.Episode
import tel.jeelpa.plugininterface.models.ShowResponse
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.repository.AnimeClient
import tel.jeelpa.plugininterface.tracker.repository.CharacterClient
import javax.inject.Inject


@HiltViewModel
class AnimeDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animeClient: AnimeClient,
    private val characterClient: CharacterClient,
    private val aniyomiPluginRepo: AniyomiPluginRepo,
//    private val parserManager: ParserManager,
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

    val characters = characterClient.getCharactersFromAnime(navArgs.id).cachedIn(viewModelScope)

    val parsers: StateFlow<List<Pair<AniyomiPluginMeta, Parser>>>
        get() = aniyomiPluginRepo.getAllPlugins()
            .mapState { list -> list.mapNotNull { it.getOrNull() } }

    // Watch Fragment Data
    private val _selectedParserId: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _searchedAnimes = MutableStateFlow(emptyList<ShowResponse>())
    private val _selectedAnime: MutableStateFlow<ShowResponse?> = MutableStateFlow(null)
    private val _episodesScraped = MutableStateFlow(emptyList<Episode>())

    private val selectedParser
        get() = parsers.value.find { it.first.uniqueId == selectedParserId.value }!!.second

    val selectedParserId
        get() = _selectedParserId.asStateFlow()
    val searchedAnimes
        get() = _searchedAnimes.asStateFlow()
    val selectedAnime
        get() = _selectedAnime.asStateFlow()
    val episodesScraped
        get() = _episodesScraped.asStateFlow()

    // TODO: Refactor these scraping functions to improve reactivity

    // onParserChange -> clear everything
    // onAnimeChange -> clear anime and episodes

    suspend fun onParserChange(parser: String) {
        _selectedParserId.value = parser
        _episodesScraped.value = emptyList() // don't wait for searching, clear episodes asap
        searchAnime(navArgs.title).join()
        val firstAnime = searchedAnimes.value.firstOrNull()
                ?: throw Exception("No Anime Found")
        onSelectAnime(firstAnime).join()
    }

    fun searchAnime(query: String) = viewModelScope.launch {
        _searchedAnimes.value = emptyList()
        _searchedAnimes.value = withContext(Dispatchers.IO){
            selectedParser.search(query)
        }
    }

    fun onSelectAnime(anime: ShowResponse) = viewModelScope.launch {
        _episodesScraped.value = emptyList()
        _selectedAnime.value = anime
        loadEpisodes()
    }

    private suspend fun loadEpisodes() = withContext(Dispatchers.IO){
        _episodesScraped.value = selectedParser.loadEpisodes(selectedAnime.value!!.link)
    }

}

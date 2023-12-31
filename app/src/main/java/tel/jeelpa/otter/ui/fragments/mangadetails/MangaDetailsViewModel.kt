package tel.jeelpa.otter.ui.fragments.mangadetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.repository.CharacterClient
import tel.jeelpa.plugininterface.tracker.repository.MangaClient
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val characterClient: CharacterClient,
    private val mangaClient: MangaClient
): ViewModel() {
    val navArgs = savedStateHandle.get<MediaCardData>("data")!!

    val mangaDetails = suspendToFlow { mangaClient.getMangaDetails(navArgs.id) }
        .cacheInScope(viewModelScope)

    val characters = characterClient.getCharactersFromManga(navArgs.id).cachedIn(viewModelScope)

}
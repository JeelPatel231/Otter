package tel.jeelpa.otter.ui.fragments.animedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tel.jeelpa.otterlib.models.MediaDetailsFull
import tel.jeelpa.otterlib.repository.AnimeClient
import javax.inject.Inject


@HiltViewModel
class AnimeDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animeClient: AnimeClient
): ViewModel() {
    val navArgs = AnimeDetailsFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _animeDetails: MutableStateFlow<MediaDetailsFull?> = MutableStateFlow(null)
    val animeDetails = _animeDetails.asStateFlow()

    private val _mediaOpenings: MutableStateFlow<List<String>?> = MutableStateFlow(null)
    val mediaOpenings = _mediaOpenings.asStateFlow()

    private val _mediaEndings: MutableStateFlow<List<String>?> = MutableStateFlow(null)
    val mediaEndings = _mediaEndings.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _animeDetails.value = animeClient.getAnimeDetails(navArgs.id)
            val malId = animeDetails.value?.idMal ?: return@launch

            _mediaOpenings.value = animeClient.getOpenings(malId)
            _mediaEndings.value = animeClient.getEndings(malId)
        }
    }

}

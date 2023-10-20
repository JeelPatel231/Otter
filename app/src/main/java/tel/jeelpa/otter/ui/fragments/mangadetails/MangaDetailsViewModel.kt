package tel.jeelpa.otter.ui.fragments.mangadetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tel.jeelpa.otterlib.models.MediaDetailsFull
import tel.jeelpa.otterlib.repository.MangaClient
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mangaClient: MangaClient
): ViewModel() {
    val navArgs = MangaDetailsFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _mangaDetails: MutableStateFlow<MediaDetailsFull?> = MutableStateFlow(null)
    val mangaDetails = _mangaDetails.asStateFlow()

    init {
        viewModelScope.launch {
            _mangaDetails.value = mangaClient.getMangaDetails(navArgs.id)
        }
    }
}
package tel.jeelpa.otter.ui.fragments.mangadetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.trackerinterface.models.MediaCardData
import tel.jeelpa.otter.trackerinterface.repository.MangaClient
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mangaClient: MangaClient
): ViewModel() {
    val navArgs = savedStateHandle.get<MediaCardData>("data")!!

    val mangaDetails = suspendToFlow { mangaClient.getMangaDetails(navArgs.id) }
        .cacheInScope(viewModelScope)

}
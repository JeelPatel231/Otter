package tel.jeelpa.otter.ui.fragments.character

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import tel.jeelpa.otterlib.repository.CharacterClient
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    characterClient: CharacterClient
): ViewModel() {
    val navArgs = savedStateHandle.get<Int>("characterId")!!

    val character = suspendToFlow { characterClient.getCharacterDetails(navArgs) }
        .cacheInScope(viewModelScope)

}


package tel.jeelpa.otter.ui.fragments.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.ui.generic.cacheInScope
import tel.jeelpa.otter.ui.generic.suspendToFlow
import tel.jeelpa.otterlib.repository.CharacterClient
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val characterClient: CharacterClient
): ViewModel() {
    fun character(characterId: Int) = suspendToFlow { characterClient.getCharacterDetails(characterId) }
        .cacheInScope(viewModelScope)

}


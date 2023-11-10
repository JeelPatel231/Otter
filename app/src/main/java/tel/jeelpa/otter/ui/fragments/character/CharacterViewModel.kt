package tel.jeelpa.otter.ui.fragments.character

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tel.jeelpa.otterlib.models.CharacterDataFull
import tel.jeelpa.otterlib.repository.CharacterClient
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    characterClient: CharacterClient
): ViewModel() {
    val navArgs = savedStateHandle.get<Int>("characterId")!!

    private val _character = MutableStateFlow<CharacterDataFull?>(null)
    val character = _character.asStateFlow()

    init {
        with(viewModelScope){
            launch { _character.value =  characterClient.getCharacterDetails(navArgs)}
        }
    }

}


package tel.jeelpa.otterlib.repository

import tel.jeelpa.otterlib.models.CharacterDataFull

interface CharacterClient {
    suspend fun getCharacterDetails(id: Int) : CharacterDataFull
}
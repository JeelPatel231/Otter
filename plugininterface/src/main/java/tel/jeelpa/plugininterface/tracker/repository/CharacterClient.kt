package tel.jeelpa.plugininterface.tracker.repository

import tel.jeelpa.plugininterface.tracker.models.CharacterDataFull

interface CharacterClient {
    suspend fun getCharacterDetails(id: Int) : CharacterDataFull
}
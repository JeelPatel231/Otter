package tel.jeelpa.otter.trackerinterface.repository

import tel.jeelpa.otter.trackerinterface.models.CharacterDataFull

interface CharacterClient {
    suspend fun getCharacterDetails(id: Int) : CharacterDataFull
}
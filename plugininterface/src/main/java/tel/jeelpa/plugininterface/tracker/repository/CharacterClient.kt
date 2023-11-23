package tel.jeelpa.plugininterface.tracker.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import tel.jeelpa.plugininterface.tracker.models.CharacterCardData
import tel.jeelpa.plugininterface.tracker.models.CharacterDataFull

interface CharacterClient {
    fun getCharactersFromAnime(id: Int): Flow<PagingData<CharacterCardData>>
    fun getCharactersFromManga(id: Int): Flow<PagingData<CharacterCardData>>

    suspend fun getCharacterDetails(id: Int) : CharacterDataFull
}
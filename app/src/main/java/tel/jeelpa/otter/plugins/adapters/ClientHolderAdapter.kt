package tel.jeelpa.otter.plugins.adapters

import kotlinx.coroutines.flow.StateFlow
import tel.jeelpa.plugininterface.tracker.repository.AnimeClient
import tel.jeelpa.plugininterface.tracker.repository.CharacterClient
import tel.jeelpa.plugininterface.tracker.repository.ClientHolder
import tel.jeelpa.plugininterface.tracker.repository.MangaClient
import tel.jeelpa.plugininterface.tracker.repository.UserClient

class ClientHolderAdapter(
    private val clientHolder: StateFlow<ClientHolder>
): ClientHolder() {
    override val uniqueId: String
        get() = clientHolder.value.uniqueId

    override val userClient: UserClient
        get() = clientHolder.value.userClient

    override val animeClient: AnimeClient
        get() = clientHolder.value.animeClient

    override val mangaClient: MangaClient
        get() = clientHolder.value.mangaClient

    override val characterClient: CharacterClient
        get() = clientHolder.value.characterClient
}

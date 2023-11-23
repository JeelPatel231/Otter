package tel.jeelpa.otter.maltrackerplugin.models

import kotlinx.serialization.Serializable
import tel.jeelpa.plugininterface.tracker.models.MediaCardData

//@Serializable
//data class AnimeList(
//    val data: List<AnimeListNode>,
//    val paging: Paging
//)

@Serializable
data class MediaListNode (
    val node: Node,
    val list_status: MyListStatus? = null
) {
    fun toMediaCardData() : MediaCardData {
        return node.toMediaCardData().copy(
            userWatched = list_status?.num_episodes_watched,
        )
    }
}
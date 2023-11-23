package tel.jeelpa.otter.maltrackerplugin.models

import kotlinx.serialization.Serializable
import tel.jeelpa.plugininterface.tracker.models.AppMediaListStatus
import tel.jeelpa.plugininterface.tracker.models.AppMediaStatus
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.models.CharacterCardData
import tel.jeelpa.plugininterface.tracker.models.CharacterDataFull
import tel.jeelpa.plugininterface.tracker.models.MediaCardData


//@Serializable
//data class NodeWrapper<T>(
//    val node: T
//)


fun mediaTypeToApp(mediaType: String): AppMediaType {
    return when(mediaType) {
        "manga" -> AppMediaType.MANGA
        "novel" -> AppMediaType.MANGA
        "one_shot" -> AppMediaType.MANGA
        "doujinshi" -> AppMediaType.MANGA
        "manhwa" -> AppMediaType.MANGA
        "manhua" -> AppMediaType.MANGA
        "oel" -> AppMediaType.MANGA

        //

        "tv" -> AppMediaType.ANIME
        "ova" -> AppMediaType.ANIME
        "movie" -> AppMediaType.ANIME
        "special" -> AppMediaType.ANIME
        "ona" -> AppMediaType.ANIME
        "music" -> AppMediaType.ANIME

        //
        else -> AppMediaType.UNKNOWN
    }
}

@Serializable
data class Node(
    val id: Int,
    val title: String,
    val main_picture: MainPicture,
    val mean: Float = 0F,
    val num_episodes: Int = 0,
    val num_chapters: Int = 0,
    val num_volumes: Int = 0,
    val nsfw: String,
    val media_type: String,
) {
    fun toMediaCardData(): MediaCardData {
        return MediaCardData(
            chapters = num_chapters,
            coverImage = main_picture.medium ?: main_picture.large,
            episodes = num_episodes,
            id = id,
            isAdult = nsfw != "white",
            meanScore = mean,
            status = AppMediaStatus.UNKNOWN,
            title = title,
            type = mediaTypeToApp(media_type),
            nextAiringEpisode = null
        )
    }
}

@Serializable
data class MainPicture(
    val medium: String? = null,
    val large: String? = null,
)

@Serializable
data class Ranking(
    val rank: Int
)

@Serializable
data class Paging(
    val previous: String? = null,
    val next: String? = null
)

@Serializable
data class MalResponse<T>(
    val data: List<T>,
    val paging: Paging
)

@Serializable
data class CharacterData(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val main_picture: MainPicture? = null,
    val biography: String? = null,
) {
    val fullName = "$first_name $last_name"

    fun toCharacterFullData(): CharacterDataFull {
        return CharacterDataFull(
            id = id,
            name = fullName,
            avatar = main_picture?.large ?: main_picture?.medium,
            age = null,
            gender = null,
            description = biography,
            dateOfBirth = null,
            media = emptyList()
        )
    }
}

@Serializable
data class CharacterNode(
    val node: CharacterData,
    val role: String,
) {
    fun toCharacterCard(): CharacterCardData {
        return CharacterCardData(
            id = node.id,
            name = node.fullName,
            role = role,
            avatar = node.main_picture?.medium ?: node.main_picture?.large
        )
    }
}


fun malWatchStatusToApp(status: String?): AppMediaListStatus {
    return when (status?.lowercase()) {
        "watching" -> AppMediaListStatus.CURRENT
        "completed" -> AppMediaListStatus.COMPLETED
        "on_hold" -> AppMediaListStatus.PAUSED
        "dropped" -> AppMediaListStatus.DROPPED
        "plan_to_watch" -> AppMediaListStatus.PLANNING
        else -> AppMediaListStatus.UNKNOWN
    }
}
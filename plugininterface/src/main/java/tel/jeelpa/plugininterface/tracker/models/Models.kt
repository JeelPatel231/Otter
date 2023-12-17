package tel.jeelpa.plugininterface.tracker.models

import kotlinx.serialization.Serializable


interface Equitable {
    override fun equals(other: Any?): Boolean
}

enum class AppMediaListStatus(value: String) {
    CURRENT("CURRENT"),
    PLANNING("PLANNING"),
    COMPLETED("COMPLETED"),
    DROPPED("DROPPED"),
    PAUSED("PAUSED"),
    REPEATING("REPEATING"),
    UNKNOWN("UNKNOWN")
}

enum class AppMediaStatus(value: String) {
    FINISHED("FINISHED"),
    RELEASING("RELEASING"),
    NOT_YET_RELEASED("NOT_YET_RELEASED"),
    CANCELLED("CANCELLED"),
    HIATUS("HIATUS"),
    UNKNOWN("UNKNOWN"),
}

enum class AppMediaType(value: String) {
    ANIME("ANIME"),
    MANGA("MANGA"),
    UNKNOWN("UNKNOWN")
}

data class AppNextAiringEpisode(
    val episode: Int,
)

//@Parcelize
data class CharacterCardData(
    val id: Int,
    val name: String,
    val avatar: String?,
    val role: String,
): Equitable


data class CharacterDataFull(
    val id: Int,
    val name: String,
    val avatar: String?,
    val age: String?,
    val gender: String?,
    val description: String?,
    val dateOfBirth: String?,
    val media: List<MediaCardData>
)

data class AppDate(
    val day: Int?,
    val month: Int?,
    val year: Int?,
) {
    override fun toString(): String {
        return "${day ?: "??"}/${month ?: "??"}/${year ?: "??"}"
    }
}

@Serializable
data class MediaCardData(
    val id: Int,
    val type: AppMediaType,
    val isAdult: Boolean,
    val status: AppMediaStatus,
    val meanScore: Float,
    val userScore: Double?, //? = null, // null when not logged in or not in list
    val userWatched: Int? = null, // null when not logged in or not in list
    val userListStatus: AppMediaListStatus, // = AppMediaListStatus.UNKNOWN,
    val coverImage: String?,
    val title: String,
    val episodes: Int?,
    val episodesAired: Int?,
    val chapters: Int?,
) : Equitable, java.io.Serializable


data class MediaRelationCardData(
    val id: Int,
    val type: AppMediaType,
    val isAdult: Boolean,
    val status: AppMediaStatus,
    val meanScore: Float,
    val userScore: Double?, //? = null, // null when not logged in or not in list
    val userWatched: Int? = null, // null when not logged in or not in list
    val userListStatus: AppMediaListStatus, // = AppMediaListStatus.UNKNOWN,
    val coverImage: String?,
    val title: String,
    val episodes: Int?,
    val episodesAired: Int?,
    val chapters: Int?,
    val relation: MediaRelationType,
): Equitable {
    fun toMediaCardData() : MediaCardData {
        return MediaCardData(
            id, type, isAdult,
            status,meanScore, userScore,
            userWatched, userListStatus,
            coverImage, title, episodes,
            episodesAired,
            chapters
        )
    }
}


fun MediaCardData.withRelation(relation: MediaRelationType): MediaRelationCardData {
    return MediaRelationCardData(
        id, type, isAdult,
        status,meanScore, userScore,
        userWatched, userListStatus,
        coverImage, title, episodes,
        episodesAired,
        chapters, relation
    )
}

enum class MediaRelationType(val value: String) {
    ADAPTATION("ADAPTATION"),
    PREQUEL("PREQUEL"),
    SEQUEL("SEQUEL"),
    PARENT("PARENT"),
    SIDE_STORY("SIDE_STORY"),
    CHARACTER("CHARACTER"),
    SUMMARY("SUMMARY"),
    ALTERNATIVE("ALTERNATIVE"),
    SPIN_OFF("SPIN_OFF"),
    OTHER("OTHER"),
    SOURCE("SOURCE"),
    COMPILATION("COMPILATION"),
    CONTAINS("CONTAINS"),
    UNKNOWN("UNKNOWN");

    companion object {
        operator fun get(value: String): MediaRelationType =
            MediaRelationType
                .values()
                .find { it.value.equals(value, ignoreCase = true) }
                ?: UNKNOWN
    }
}


data class MediaDetailsFull(
    val id: Int,
    val idMal: Int?,
    val nextAiringEpisode: Int?,
    val countryOfOrigin: String,
    val isAdult: Boolean,
    val isFavourite: Boolean,
    val type: AppMediaType,
    val bannerImage: String?,
    val coverImage: String,
    val title: String,
    val meanScore: Float,
    val status: AppMediaStatus,
    val episodes: Int?,
    val chapters: Int?,
    val duration: Int?,
    val format: String,
    val source: String,
    val studios: List<String>,
    val season: String?,
    val startDate: AppDate?,
    val endDate: AppDate?,
    val description: String,
    val synonyms: List<String>,
    val trailer: AppTrailer?,
    val genres: List<String>,
    val tags: List<String>,
    val recommendation: List<MediaCardData>,
    val relations: List<MediaRelationCardData>,
//    val characters: List<CharacterCardData>
)

data class AppTrailer(
    val id: String,
    val service: String,
    val thumbnail: String,
)

data class User(
    val userId: Int,
    val username: String,
    val profileImage: String?,
    val bannerImage: String?,
    val episodeCount: Int,
    val chapterCount: Int,
)

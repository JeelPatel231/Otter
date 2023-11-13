package tel.jeelpa.otterlib.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import tel.jeelpa.otter.CurrentUserMediaQuery
import tel.jeelpa.otter.GetCharacterDataQuery
import tel.jeelpa.otter.type.MediaListStatus
import tel.jeelpa.otter.type.MediaStatus
import tel.jeelpa.otter.type.MediaType


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

@Parcelize
data class CharacterCardData(
    val id: Int,
    val name: String,
    val avatar: String,
    val role: String,
): Parcelable, Equitable


data class CharacterDataFull(
    val id: Int,
    val name: String,
    val avatar: String,
    val age: String?,
    val gender: String?,
    val description: String?,
    val dateOfBirth: String?,
    val media: List<MediaCardData>
)

fun GetCharacterDataQuery.DateOfBirth.toApp(): String {
    if((day ?: month ?: year) == null){
        return "??"
    }
    return "$day/$month/$year"
}

data class AppDate(
    val day: Int?,
    val month: Int?,
    val year: Int?,
) {
    override fun toString(): String {
        return "${day ?: "??"}/${month ?: "??"}/${year ?: "??"}"
    }
}

@Parcelize
data class MediaCardData(
    val id: Int,
    val type: AppMediaType,
    val isAdult: Boolean,
    val status: AppMediaStatus,
    val meanScore: Float,
    val coverImage: String,
    val title: String,
    val episodes: Int?,
    val nextAiringEpisode: Int?,
    val chapters: Int?,
) : Parcelable, Equitable


data class MediaRelationCardData(
    val id: Int,
    val type: AppMediaType,
    val isAdult: Boolean,
    val status: AppMediaStatus,
    val meanScore: Float,
    val coverImage: String,
    val title: String,
    val episodes: Int?,
    val nextAiringEpisode: Int?,
    val chapters: Int?,
    val relation: MediaRelationType,
): Equitable {
    fun toMediaCardData() : MediaCardData {
        return MediaCardData(
            id, type, isAdult,
            status,meanScore,
            coverImage, title,
            episodes,
            nextAiringEpisode,
            chapters
        )
    }
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
        operator fun get(value: String): MediaRelationType = MediaRelationType.values().find { it.value == value } ?: UNKNOWN
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
    val season: String,
    val startDate: AppDate?,
    val endDate: AppDate?,
    val description: String,
    val synonyms: List<String>,
    val trailer: AppTrailer?,
    val genres: List<String>,
    val tags: List<String>,
    val recommendation: List<MediaCardData>,
    val relations: List<MediaRelationCardData>,
    val characters: List<CharacterCardData>
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

@Serializable
class AnilistRequestBody(
    val grant_type: String,
    val client_id: String,
    val client_secret: String,
    val redirect_uri: String,
    val code: String,
)

@Serializable
class AnilistResponseBody(
    val token_type: String,
    val expires_in: Long,
    val access_token: String,
    val refresh_token: String,
)


fun MediaListStatus?.toApp(): AppMediaListStatus {
    return when (this) {
        MediaListStatus.CURRENT -> AppMediaListStatus.CURRENT
        MediaListStatus.COMPLETED -> AppMediaListStatus.COMPLETED
        MediaListStatus.DROPPED -> AppMediaListStatus.DROPPED
        MediaListStatus.PAUSED -> AppMediaListStatus.PAUSED
        MediaListStatus.PLANNING -> AppMediaListStatus.PLANNING
        MediaListStatus.REPEATING -> AppMediaListStatus.REPEATING
        MediaListStatus.UNKNOWN__, null -> AppMediaListStatus.UNKNOWN
    }
}

fun MediaStatus?.toApp(): AppMediaStatus {
    return when (this) {
        MediaStatus.HIATUS -> AppMediaStatus.HIATUS
        MediaStatus.CANCELLED -> AppMediaStatus.CANCELLED
        MediaStatus.NOT_YET_RELEASED -> AppMediaStatus.NOT_YET_RELEASED
        MediaStatus.FINISHED -> AppMediaStatus.FINISHED
        MediaStatus.RELEASING -> AppMediaStatus.RELEASING
        MediaStatus.UNKNOWN__, null -> AppMediaStatus.UNKNOWN
    }
}

fun MediaType?.toApp(): AppMediaType {
    return when (this) {
        MediaType.ANIME -> AppMediaType.ANIME
        MediaType.MANGA -> AppMediaType.MANGA
        MediaType.UNKNOWN__, null -> AppMediaType.UNKNOWN
    }
}

fun CurrentUserMediaQuery.NextAiringEpisode.toApp() =
    AppNextAiringEpisode(this.episode)

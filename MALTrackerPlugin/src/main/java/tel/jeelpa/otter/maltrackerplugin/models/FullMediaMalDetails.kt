package tel.jeelpa.otter.maltrackerplugin.models

import kotlinx.serialization.Serializable
import tel.jeelpa.plugininterface.tracker.models.AppMediaStatus
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull
import tel.jeelpa.plugininterface.tracker.models.MediaRelationCardData
import tel.jeelpa.plugininterface.tracker.models.MediaRelationType
import tel.jeelpa.plugininterface.tracker.models.withRelation

@Serializable
data class FullMediaMalDetails(
    val id: Int,
    val title: String,
    val alternative_titles: AltTitles,
    val main_picture: MainPicture,
    val start_date: String? = null,
    val end_date: String? = null,
    val synopsis: String,
    val start_season: Season? = null,
    val mean: Float,
    val rank: Int,
    val popularity: Int,
    val num_list_users: Long,
    val num_scoring_users: Long,
    val nsfw: String,
    val created_at: String, //date String
    val updated_at: String, // date String
    val media_type: String,
    val status: String,
    val genres: List<Genre>,
    val my_list_status: MyListStatus? = null, // when not logged in
    val num_episodes: Int = 0,
    val num_volumes: Int = 0,
    val num_chapters: Int = 0,
    val average_episode_duration: Long? = null,
    val pictures: List<MainPicture>,
    val background: String? = null,
    val opening_themes: List<SongTheme> = emptyList(),
    val ending_themes: List<SongTheme> = emptyList(),
    val related_anime : List<RelatedAnime> = emptyList(),
    val related_manga : List<RelatedAnime> = emptyList(),
    val recommendations: List<Recommendations>,
    val studios: List<Studio> = emptyList(),
){
    fun toMediaFullDetails(type: AppMediaType): MediaDetailsFull {
        return MediaDetailsFull(
            id = id,
            idMal = id,
            nextAiringEpisode = null,
            countryOfOrigin = "??",
            isAdult = nsfw != "white",
            isFavourite = false,
            type = type,
            bannerImage = background,
            coverImage = main_picture.medium ?: main_picture.large!!,
            title = title,
            meanScore = mean,
            status = toAppMediaStatus(status),
            episodes = num_episodes,
            chapters = num_chapters,
            duration = average_episode_duration?.toInt(), // TODO, MAKE DURATION LONG
            format = media_type, // IDK???
            source = "??",
            studios = studios.map { it.name },
            season = start_season?.let { "${it.season} ${it.year}"},
            startDate = null, // TODO
            endDate = null, // TODO
            description = synopsis,
            synonyms = alternative_titles.synonyms,
            trailer = null,
            genres = genres.map { it.name },
            tags = emptyList(), // TODO
            recommendation = recommendations.map { it.node.toMediaCardData() },
            relations = related_anime.map { it.toMediaListRelation() } + related_manga.map { it.toMediaListRelation() },
        )
    }
}

fun toAppMediaStatus(status: String) : AppMediaStatus {
    return when(status) {
        "finished_airing" ->  AppMediaStatus.FINISHED
        "currently_airing" -> AppMediaStatus.RELEASING
        "not_yet_aired" -> AppMediaStatus.NOT_YET_RELEASED
        else -> AppMediaStatus.UNKNOWN
    }
}

@Serializable
data class SongTheme(
    val id: Int,
    val anime_id: Int,
    val text: String
)

@Serializable
data class AltTitles(
    val synonyms: List<String> = emptyList(),
    val en: String? = null,
    val jp: String? = null
)

@Serializable
data class Studio(
    val id: Int,
    val name: String,
)

@Serializable
data class Season(
    val year: Int,
    val season: String
)

@Serializable
data class RelatedAnime(
    val node: Node,
    val relation_type: String,
    val relation_type_formatted: String
){
    fun toMediaListRelation() : MediaRelationCardData {
        return node.toMediaCardData().withRelation(MediaRelationType[relation_type])
    }
}

@Serializable
data class Recommendations(
    val node: Node,
    val num_recommendations: Int,
)

@Serializable
data class Genre(
    val id: Int,
    val name: String,
)

@Serializable
data class MyListStatus(
    val status: String, // MAP TO APP TYPE
    val score: Float? = null,
    val num_episodes_watched: Int? = null,
    val is_rewatching: Boolean? = null,
    val updated_at: String? = null, // Date String
)
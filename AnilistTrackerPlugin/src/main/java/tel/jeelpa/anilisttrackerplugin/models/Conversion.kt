package tel.jeelpa.anilisttrackerplugin.models

import tel.jeelpa.otter.anilisttrackerplugin.models.CurrentUserMediaQuery
import tel.jeelpa.otter.anilisttrackerplugin.models.GetCharacterDataQuery
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaListStatus
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaStatus
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaType
import tel.jeelpa.otter.trackerinterface.models.AppMediaListStatus
import tel.jeelpa.otter.trackerinterface.models.AppMediaStatus
import tel.jeelpa.otter.trackerinterface.models.AppMediaType
import tel.jeelpa.otter.trackerinterface.models.AppNextAiringEpisode

fun GetCharacterDataQuery.DateOfBirth.toApp(): String {
    if((day ?: month ?: year) == null){
        return "??"
    }
    return "$day/$month/$year"
}


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

package tel.jeelpa.plugininterface.tracker.models

import java.time.LocalDate

data class UserMediaAnime(
    val status: AppMediaListStatus,
    val watched: Int,
    val score: Float?,
    val startDate: LocalDate?,
    val finishDate: LocalDate?,
)

data class UserMediaManga(
    val status: AppMediaListStatus,
//    val volumes: Int,
    val chapters: Int,
    val score: Float?,
    val startDate: LocalDate?,
    val finishDate: LocalDate?,
)

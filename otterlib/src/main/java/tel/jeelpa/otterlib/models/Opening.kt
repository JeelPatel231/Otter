package tel.jeelpa.otterlib.models

data class MediaOpEd(
    val name: String,
    val author: String,
    val link: String?,
)

data class MalMediaScrapedDetails(
    val name: String,
    val malId: Int,
    val type: String,
    val openings: List<String>,
    val endings: List<String>,
)
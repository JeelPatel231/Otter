package tel.jeelpa.otter.reference.models

import kotlinx.serialization.Serializable

/**
 * A single show which contains some episodes/chapters which is sent by the site using their search function.
 *
 * You might want to include `otherNames` & `total` too, to further improve user experience.
 *
 * You can also store a Map of Strings if you want to save some extra data.
 * **/
@Serializable
data class ShowResponse(
    val name: String,
    val link: String,
    val coverUrl: FileUrl,

    //would be Useful for custom search, ig
    val otherNames: List<String> = listOf(),

    //Total number of Episodes/Chapters in the show.
    val total: Int? = null,

    //In case you want to send some extra data
    val extra: Map<String, String> = emptyMap(),
) {
    constructor(
        name: String,
        link: String,
        coverUrl: String,
        otherNames: List<String> = emptyList(),
        total: Int? = null,
        extra: Map<String, String> = emptyMap()
    ) : this(
        name, link, FileUrl(coverUrl), otherNames, total, extra
    )
}
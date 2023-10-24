package tel.jeelpa.otter.reference.models

import kotlinx.serialization.Serializable

/**
 * A url, which can also have headers
 * **/
@Serializable
data class FileUrl(
    val url: String,
    val headers: Map<String, String> = mapOf()
) {
    companion object {
        operator fun get(url: String, headers: Map<String, String> = mapOf()): FileUrl {
            return FileUrl(url, headers)
        }
    }
}

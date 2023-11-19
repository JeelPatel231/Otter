package tel.jeelpa.plugininterface.models

import kotlinx.serialization.Serializable

/**
 * A url, which can also have headers
 * **/
@Serializable
data class FileUrl(
    val url: String,
    val headers: Map<String, String> = mapOf()
) : java.io.Serializable {
    companion object {
        operator fun get(url: String, headers: Map<String, String> = mapOf()): FileUrl {
            return FileUrl(url, headers)
        }
    }
}

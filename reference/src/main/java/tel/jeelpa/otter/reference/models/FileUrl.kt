package tel.jeelpa.otter.reference.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * A url, which can also have headers
 * **/
@Serializable
@Parcelize
data class FileUrl(
    val url: String,
    val headers: Map<String, String> = mapOf()
) : Parcelable {
    companion object {
        operator fun get(url: String, headers: Map<String, String> = mapOf()): FileUrl {
            return FileUrl(url, headers)
        }
    }
}

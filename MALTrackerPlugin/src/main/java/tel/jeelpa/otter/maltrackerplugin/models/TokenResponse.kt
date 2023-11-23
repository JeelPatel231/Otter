package tel.jeelpa.otter.maltrackerplugin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class ResponseToken(
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expireDuration: Long,
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
)

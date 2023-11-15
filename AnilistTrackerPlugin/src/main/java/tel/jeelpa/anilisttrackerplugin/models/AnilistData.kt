package tel.jeelpa.anilisttrackerplugin.models

import kotlinx.serialization.Serializable

data class AnilistData(
    val id: String,
    val secret: String,
    val redirectUri: String,
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


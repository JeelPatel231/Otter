package tel.jeelpa.otterlib.models
data class AnilistData(
    val id: String,
    val secret: String,
    val redirectUri: String,
)

val ANILIST_DATA = AnilistData(
    "6604",
    "15UaWKmaJtkxoUGloCFq4zEwRaM9AHHtQ2nQXiJ1",
    "otter://logintracker/anilist"
)
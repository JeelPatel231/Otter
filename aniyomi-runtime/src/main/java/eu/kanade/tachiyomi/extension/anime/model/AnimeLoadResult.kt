package eu.kanade.tachiyomi.extension.anime.model

sealed interface AnimeLoadResult {
    class Success(val extension: AnimeExtension.Installed) : AnimeLoadResult
    class Untrusted(val extension: AnimeExtension.Untrusted) : AnimeLoadResult
    object Error : AnimeLoadResult
}

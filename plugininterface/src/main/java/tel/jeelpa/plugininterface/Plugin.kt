package tel.jeelpa.plugininterface

import okhttp3.OkHttpClient
import tel.jeelpa.plugininterface.anime.extractor.Extractor
import tel.jeelpa.plugininterface.anime.parser.Parser
import tel.jeelpa.plugininterface.storage.UserStorage
import tel.jeelpa.plugininterface.tracker.repository.ClientHolder

interface PluginRegistrar {
    // implements code for registering stuff in app
    fun registerAnimeExtractor(vararg extractors: Extractor)

    fun registerAnimeParser(vararg parsers: Parser)

    fun registerTracker(vararg trackers: ClientHolder)

    fun registerMangaScraper(vararg scraper: Any?) // future
}

interface LoadablePlugin {
    fun load()
}

interface AppGivenDependencies {
    val okHttpClient: OkHttpClient
    // todo: Scope these depedencies to their owners, no other
    // plugin should be able to use this
    val userStorage: UserStorage
}
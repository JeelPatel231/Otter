package tel.jeelpa.otter.plugins

import android.content.Context
import dalvik.system.DexClassLoader
import tel.jeelpa.plugininterface.AppGivenDependencies
import tel.jeelpa.plugininterface.LoadablePlugin
import tel.jeelpa.plugininterface.PluginRegistrar
import tel.jeelpa.plugininterface.anime.extractor.Extractor
import tel.jeelpa.plugininterface.anime.parser.Parser
import tel.jeelpa.plugininterface.tracker.repository.ClientHolder
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

class PluginRegistrarImpl(
    private val parserManager: ParserManager,
    private val trackerManager: TrackerManager,
    private val extractorManager: ExtractorManager,
): PluginRegistrar {
    override fun registerAnimeExtractor(vararg extractors: Extractor) {
        extractors.forEach { extractorManager.registerExtractor(it) }
    }

    override fun registerAnimeParser(vararg parsers: Parser) {
        parsers.forEach { parserManager.registerParser(it) }
    }

    override fun registerTracker(vararg trackers: ClientHolder) {
        trackers.forEach { trackerManager.registerTracker(it) }
    }

    override fun registerMangaScraper(vararg scraper: Any?) {
        TODO("Not yet implemented")
    }

}

class PluginInitializer (
    private val context: Context,
    private val pluginRegistrar: PluginRegistrar,
    private val appGivenDependencies: AppGivenDependencies,
){
    private fun getNamedClassConstructor(name: String, classLoader: DexClassLoader): KFunction<Any> {
        return Class.forName(name, true, classLoader)
            .kotlin
            .primaryConstructor!!
    }

    operator fun invoke(pathToJar: String) {
        val classLoader = DexClassLoader(
            pathToJar,
            context.codeCacheDir.absolutePath, // can be null, but crashes with NPE on Nougat
            null,
            context.classLoader
        )

        val pluginObj = getNamedClassConstructor("Metadata", classLoader).also { println(it) }
            .call(pluginRegistrar, appGivenDependencies) as LoadablePlugin

        pluginObj.load()
    }
}

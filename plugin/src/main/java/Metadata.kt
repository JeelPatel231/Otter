
import tel.jeelpa.plugin.extractors.DummyExtractor
import tel.jeelpa.plugin.parsers.DummyParser
import tel.jeelpa.plugininterface.AppGivenDependencies
import tel.jeelpa.plugininterface.LoadablePlugin
import tel.jeelpa.plugininterface.PluginRegistrar


class Metadata(
    private val pluginRegistrar: PluginRegistrar,
    deps: AppGivenDependencies
) : LoadablePlugin {
    override fun load() {
        pluginRegistrar.registerAnimeExtractor(
            DummyExtractor()
        )

        pluginRegistrar.registerAnimeParser(
            DummyParser()
        )
    }
}
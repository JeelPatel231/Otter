
import tel.jeelpa.otter.maltrackerplugin.data.MalClientHolder
import tel.jeelpa.plugininterface.AppGivenDependencies
import tel.jeelpa.plugininterface.LoadablePlugin
import tel.jeelpa.plugininterface.PluginRegistrar

class Metadata(
    private val pluginRegistrar: PluginRegistrar,
    deps: AppGivenDependencies
) : LoadablePlugin {

    private val malTracker = MalClientHolder(deps.okHttpClient, deps.userStorage)

    override fun load() {
        pluginRegistrar.registerTracker(malTracker)
    }
}

import tel.jeelpa.anilisttrackerplugin.data.ClientHolderImpl
import tel.jeelpa.plugininterface.AppGivenDependencies
import tel.jeelpa.plugininterface.LoadablePlugin
import tel.jeelpa.plugininterface.PluginRegistrar

class Metadata(
    private val pluginRegistrar: PluginRegistrar,
    deps: AppGivenDependencies
) : LoadablePlugin {

    private val anilistTracker = ClientHolderImpl(deps.userStorage)

    override fun load() {
        pluginRegistrar.registerTracker(anilistTracker)
    }
}
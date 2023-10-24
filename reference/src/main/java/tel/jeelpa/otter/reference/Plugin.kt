package tel.jeelpa.otter.reference

interface Plugin {
    fun register()
}

interface PluginMetadata {
    val plugins: List<Plugin>
}
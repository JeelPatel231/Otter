package tel.jeelpa.aniyomi_runtime

import android.app.Application
import android.content.Context
import android.content.Intent
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.extension.anime.model.AnimeLoadResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import tel.jeelpa.aniyomi_runtime.adapter.AnimeCatalogueSourceToParser
import tel.jeelpa.plugger.PluginRepo
import tel.jeelpa.plugger.pluginloader.apk.ExtensionsUpdatedReceiver
import tel.jeelpa.plugininterface.anime.parser.Parser
import uy.kohesive.injekt.Injekt

data class AniyomiPluginMeta(
    val displayName: String,
    val uniqueId: String,
){
    override fun toString(): String =
        displayName
}

class AniyomiPluginRepo(
    private val appContext: Application
) : PluginRepo<AniyomiPluginMeta, Parser> {

    init {
        val listener = object: ExtensionsUpdatedReceiver(appContext) {
            override fun onReceive(context: Context?, intent: Intent?) {
                loaded.update { loadAniyomiExtensions() }
            }
        }
        listener.register()

        Injekt.importModule(AppModule(appContext))
    }

    private val extensionLoader = AnimeExtensionLoader()

    private fun loadAniyomiExtensions(): List<Result<Pair<AniyomiPluginMeta, AnimeCatalogueSourceToParser>>> {
        return extensionLoader.loadExtensions(appContext)
            .asSequence()
            .onEach {
                when(it){
                    // TODO: map to result types
                    is AnimeLoadResult.Success -> println("LOADED ${it.extension}")
                    is AnimeLoadResult.Error -> println("Failed to load: $it")
                    is AnimeLoadResult.Untrusted -> error("Unreachable")
                }
            }
            .filterIsInstance<AnimeLoadResult.Success>()
            .map { it.extension }
            .flatMap { it.sources.map { src ->
                val meta = AniyomiPluginMeta(it.name, "${it.pkgName}.${src.id}")
                Pair(meta, src) }
            }
            .filter { it.second is AnimeCatalogueSource }
            .map { Pair(it.first, AnimeCatalogueSourceToParser(it.second as AnimeCatalogueSource)) }
            .map { Result.success(it) }
            .toList()
    }

    private val loaded = MutableStateFlow(loadAniyomiExtensions())

    override fun getAllPlugins(): StateFlow<List<Result<Pair<AniyomiPluginMeta, Parser>>>> {
        return loaded.asStateFlow()
    }
}
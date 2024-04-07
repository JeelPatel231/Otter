package tel.jeelpa.aniyomi_runtime

import android.app.Application
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.extension.anime.model.AnimeLoadResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import tel.jeelpa.plugger.PluginRepo
import uy.kohesive.injekt.Injekt

class AniyomiPluginRepo(
    private val appContext: Application
) : PluginRepo<AnimeCatalogueSource> {

    init {
        Injekt.importModule(AppModule(appContext))
    }

    private val extensionLoader = AnimeExtensionLoader()

    private val loaded = extensionLoader.loadExtensions(appContext)
        .onEach {
            when(it){
                // TODO: map to result types
                is AnimeLoadResult.Success -> println("LOADED ${it.extension}")
                is AnimeLoadResult.Error -> println("Failed to load: $it")
                is AnimeLoadResult.Untrusted -> error("Unreachable")
            }
        }
        .filterIsInstance<AnimeLoadResult.Success>()
        .flatMap { it.extension.sources }
        .filterIsInstance<AnimeCatalogueSource>()
        .map { Result.success(it) }

    override fun getAllPlugins(): Flow<List<Result<AnimeCatalogueSource>>> {
        return flowOf(loaded)
    }
}
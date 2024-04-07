package tel.jeelpa.aniyomi_runtime

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.extension.anime.model.AnimeLoadResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import uy.kohesive.injekt.Injekt

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() = runBlocking {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("tel.jeelpa.aniyomi_runtime.test", appContext.packageName)

        Injekt.importModule(AppModule(appContext.applicationContext as Application))
        val extensions = AnimeExtensionLoader()
            .loadExtensions(appContext)
            .asSequence()
            .filterIsInstance<AnimeLoadResult.Success>()
            .flatMap { it.extension.sources }
            .filterIsInstance<AnimeCatalogueSource>()

        val allAnime = extensions.first()

        val popularAnime = allAnime.getPopularAnime(1)

        val onePiece = popularAnime.animes.first()
        val opEpisodes = allAnime.getEpisodeList(onePiece).also { println(it.size) }

        val ep1 = opEpisodes.first()

        val videoList = allAnime.getVideoList(ep1)

        val firstVideo = videoList.first()

        println(firstVideo.url)
        println(firstVideo.videoUrl)
    }
}
package tel.jeelpa.otter.di

import android.app.Application
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import okhttp3.OkHttpClient
import tel.jeelpa.otter.plugins.PluginInitializer
import tel.jeelpa.otter.reference.ExtractorManager
import tel.jeelpa.otter.reference.ParserManager
import tel.jeelpa.otter.reference.RegisterExtractorUseCase
import tel.jeelpa.otter.reference.RegisterParserUseCase
import tel.jeelpa.otter.reference.RegisterUseCase
import tel.jeelpa.otter.ui.generic.CreateMediaSourceFromUri
import tel.jeelpa.otter.ui.markwon.SpoilerPlugin
import java.io.File
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DIModule {

    @Provides
    @Singleton
    fun providesMarkwon(application: Application) : Markwon {
        return Markwon.builder(application)
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            .usePlugin(SpoilerPlugin())
            .build()
    }

    @Provides
    @Singleton
    fun providesHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
//            .ignoreAllSSLErrors()
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
        // TODO: add DNS
    }


    @Provides
    @Singleton
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun provideSimpleVideoCache(application: Application): SimpleCache {
        val databaseProvider = StandaloneDatabaseProvider(application)
        return SimpleCache(
            File(application.cacheDir, "exoplayer").also { it.deleteOnExit() }, // Ensures always fresh file
            LeastRecentlyUsedCacheEvictor(300L * 1024L * 1024L),
            databaseProvider
        )
    }

    @Provides
    @Singleton
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun providesCreateMediaSourceFromUri(
        okHttpClient: OkHttpClient,
        simpleVideoCache: SimpleCache
    ) : CreateMediaSourceFromUri {
        return CreateMediaSourceFromUri(okHttpClient, simpleVideoCache)
    }


    // plugin manager
    @Provides
    @Singleton
    fun providesParserManager(): ParserManager {
        return ParserManager()
    }

    @Provides
    @Singleton
    fun providesExtractorManager(): ExtractorManager {
        return ExtractorManager()
    }

    @Provides
    @Singleton
    fun providesPluginInstantiator(
        application: Application,
        httpClient: OkHttpClient,
        extractorManager: ExtractorManager,
        parserManager: ParserManager
    ): PluginInitializer {
        val registerUseCase = RegisterUseCase(
            RegisterExtractorUseCase(extractorManager),
            RegisterParserUseCase(parserManager),
        )
        return PluginInitializer(application, httpClient, registerUseCase)
    }

    @Provides
    @Singleton
    fun providesExoplayer(
        application: Application
    ) : ExoPlayer {
        return ExoPlayer.Builder(application).build()
    }

}


//@Module
//@InstallIn(FragmentComponent::class)
//class DIFragmentModule {
//
//    @Provides
//    @FragmentScoped
//    fun providesExoplayer(application: Application) : ExoPlayer {
//        return ExoPlayer.Builder(application).build()
//    }
//}
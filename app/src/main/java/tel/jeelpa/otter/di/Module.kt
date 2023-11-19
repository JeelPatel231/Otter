package tel.jeelpa.otter.di

import android.annotation.SuppressLint
import android.app.Application
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.components.SingletonComponent
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import okhttp3.OkHttpClient
import tel.jeelpa.otter.plugins.ExtractorManager
import tel.jeelpa.otter.plugins.ParserManager
import tel.jeelpa.otter.plugins.PluginInitializer
import tel.jeelpa.otter.plugins.PluginRegistrarImpl
import tel.jeelpa.otter.ui.generic.CreateMediaSourceFromUri
import tel.jeelpa.otter.ui.generic.CreateMediaSourceFromVideo
import tel.jeelpa.otter.ui.markwon.SpoilerPlugin
import tel.jeelpa.plugininterface.AppGivenDependencies
import tel.jeelpa.plugininterface.PluginRegistrar
import tel.jeelpa.plugininterface.storage.UserStorage
import tel.jeelpa.plugininterface.tracker.TrackerManager
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Module
@InstallIn(SingletonComponent::class)
class DIModule {

    @Provides
    @Singleton
    fun providesMarkwon(application: Application): Markwon {
        return Markwon.builder(application)
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            .usePlugin(SpoilerPlugin())
            .build()
    }

    @Provides
    @Singleton
    fun providesHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .ignoreAllSSLErrors()
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }


    @Provides
    @Singleton
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun provideSimpleVideoCache(application: Application): SimpleCache {
        val databaseProvider = StandaloneDatabaseProvider(application)
        return SimpleCache(
            File(
                application.cacheDir,
                "exoplayer"
            ).also { it.deleteOnExit() }, // Ensures always fresh file
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
    ): CreateMediaSourceFromVideo {
        val uriSource = CreateMediaSourceFromUri(okHttpClient, simpleVideoCache)
        return CreateMediaSourceFromVideo(uriSource)
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
    fun providesPluginDependencies(
        okHttpClient: OkHttpClient,
        userStorage: UserStorage
    ) : AppGivenDependencies {
        return object : AppGivenDependencies {
            override val okHttpClient = okHttpClient
            override val userStorage = userStorage
        }
    }

    @Provides
    @Singleton
    fun providesPluginRegistrar(
        parserManager: ParserManager,
        trackerManager: TrackerManager,
        extractorManager: ExtractorManager,
    ): PluginRegistrar {
        return PluginRegistrarImpl(
            parserManager,
            trackerManager,
            extractorManager,
        )
    }

    @Provides
    @Singleton
    fun providesPluginInstantiator(
        application: Application,
        pluginRegistrar: PluginRegistrar,
        appGivenDependencies: AppGivenDependencies,
    ): PluginInitializer {
        return PluginInitializer(
            application,
            pluginRegistrar,
            appGivenDependencies,
        )
    }

}


@Module
@InstallIn(FragmentComponent::class)
class DIFragmentModule {

    @Provides
    @FragmentScoped
    fun providesExoplayer(application: Application): ExoPlayer {
        return ExoPlayer.Builder(application).build()
    }
}


// https://stackoverflow.com/a/59322754
fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {

    @SuppressLint("CustomX509TrustManager")
    val naiveTrustManager = object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
    }

    val insecureSocketFactory = SSLContext.getInstance("SSL").apply {
        val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
        init(null, trustAllCerts, SecureRandom())
    }.socketFactory

    sslSocketFactory(insecureSocketFactory, naiveTrustManager)
    hostnameVerifier { _, _ -> true }
    return this
}
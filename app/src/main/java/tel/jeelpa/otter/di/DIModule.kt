package tel.jeelpa.otter.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import tel.jeelpa.otter.BuildConfig
import tel.jeelpa.otter.factories.TrackerClientFactory
import tel.jeelpa.otterlib.data.LoginImpl
import tel.jeelpa.otterlib.models.AnilistData
import tel.jeelpa.otterlib.repository.LoginProcedure
import tel.jeelpa.otterlib.store.UserStore
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DILibModule {

    @Provides
    @Singleton
    fun providesAnilistData(): AnilistData {
        return AnilistData(
            BuildConfig.ANILIST_ID,
            BuildConfig.ANILIST_SECRET,
            BuildConfig.ANILIST_REDIRECT_URI
        )
    }

    @Provides
    @Singleton
    fun providesLoginProcedureUseCase(
        application: Application,
        anilistData: AnilistData,
    ): LoginProcedure {
        return LoginImpl(
            application,
            anilistData.id,
            anilistData.redirectUri
        )
    }

    @Provides
    @Singleton
    fun providesUserStore(application: Application) : UserStore {
        return UserStore(application)
    }

    @Provides
    @Singleton
    fun providesTrackerFactory(
        httpClient: OkHttpClient,
        userStore: UserStore,
        anilistData: AnilistData,
    ) : TrackerClientFactory {
        return TrackerClientFactory(
            userStore,
            httpClient,
            anilistData,
        )
    }
}

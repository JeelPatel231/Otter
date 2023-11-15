package tel.jeelpa.otter.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import tel.jeelpa.otter.models.TrackerStoreImpl
import tel.jeelpa.otter.models.UserStore
import tel.jeelpa.otter.trackerinterface.TrackerManager
import tel.jeelpa.otter.trackerinterface.repository.AnimeClient
import tel.jeelpa.otter.trackerinterface.repository.CharacterClient
import tel.jeelpa.otter.trackerinterface.repository.MangaClient
import tel.jeelpa.otter.trackerinterface.repository.UserStorage
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DILibModule {

//    @Provides
//    @Singleton
//    fun providesAnilistData(): AnilistData {
//        return AnilistData(
//            BuildConfig.ANILIST_ID,
//            BuildConfig.ANILIST_SECRET,
//            BuildConfig.ANILIST_REDIRECT_URI
//        )
//    }

//    @Provides
//    @Singleton
//    fun providesLoginProcedureUseCase(
//        application: Application,
//        anilistData: AnilistData,
//    ): tel.jeelpa.otter.trackerinterface.repository.LoginProcedure {
//        return tel.jeelpa.anilisttrackerplugin.data.LoginImpl(
//            application,
//            anilistData.id,
//            anilistData.redirectUri
//        )
//    }

    @Provides
    @Singleton
    fun providesUserStore(application: Application): UserStorage {
        return UserStore(application)
    }

    @Provides
    @Singleton
    fun providesTrackerManager(
        application: Application
    ): TrackerManager {
        return TrackerManager(TrackerStoreImpl(application))
    }

}

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelScopeClients {
    @Provides
    @ViewModelScoped
    fun providesAnimeClient(
        trackerManager: TrackerManager
    ): AnimeClient {
//        return trackerManager.trackers.first().animeClient
        return runBlocking {
            trackerManager.getCurrentTracker().first()!!.animeClient
        }
    }


    @Provides
    @ViewModelScoped
    fun providesMangaClient(
        trackerManager: TrackerManager
    ): MangaClient {
//        return trackerManager.trackers.first().mangaClient
        return runBlocking {
            trackerManager.getCurrentTracker().first()!!.mangaClient
        }
    }

    @Provides
    @ViewModelScoped
    fun providesCharacterClient(
        trackerManager: TrackerManager
    ): CharacterClient {
//        return trackerManager.trackers.first().characterClient
        return runBlocking {
            trackerManager.getCurrentTracker().first()!!.characterClient
        }
    }
}

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
import tel.jeelpa.otter.trackerinterface.TrackerStore
import tel.jeelpa.otter.trackerinterface.repository.AnimeClient
import tel.jeelpa.otter.trackerinterface.repository.CharacterClient
import tel.jeelpa.otter.trackerinterface.repository.ClientHolder
import tel.jeelpa.otter.trackerinterface.repository.MangaClient
import tel.jeelpa.otter.trackerinterface.repository.UserClient
import tel.jeelpa.otter.trackerinterface.repository.UserStorage
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DILibModule {

    @Provides
    @Singleton
    //PRIVATE
    fun providesUserStore(application: Application): UserStorage {
        return UserStore(application)
    }

    @Provides
    @Singleton
    fun providesTrackerStore(application: Application): TrackerStore {
        return TrackerStoreImpl(application)
    }

    @Provides
    @Singleton
    // PRIVATE
    // SHOULD NEVER BE CALLED IN APP CODE, ONLY IN HILT MODULES
    fun providesTrackerManager(): TrackerManager {
        return TrackerManager()
    }

    @Provides
    @Singleton
    @Named("CurrentTrackerName")
    fun providesCurrentTrackerNameOnAppStart(
        trackerStore: TrackerStore,
    ): String {
        return runBlocking {
            trackerStore.getTracker().first()!!
        }
    }

    @Provides
    @Singleton
    //Private
    fun providesClientHolder(
        trackerManager: TrackerManager,
        @Named("CurrentTrackerName") trackerName: String,
    ): ClientHolder {
        return trackerManager.getTracker(trackerName)
    }

}

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelScopeClients {
    @Provides
    @ViewModelScoped
    //Private
    fun providesUserClient(clientHolder: ClientHolder): UserClient =
        clientHolder.userClient

    @Provides
    @ViewModelScoped
    fun providesAnimeClient(clientHolder: ClientHolder): AnimeClient =
        clientHolder.animeClient


    @Provides
    @ViewModelScoped
    fun providesMangaClient(clientHolder: ClientHolder): MangaClient =
        clientHolder.mangaClient

    @Provides
    @ViewModelScoped
    fun providesCharacterClient(clientHolder: ClientHolder): CharacterClient =
        clientHolder.characterClient
}

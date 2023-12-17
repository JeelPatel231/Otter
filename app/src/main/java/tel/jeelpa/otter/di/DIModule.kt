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
import tel.jeelpa.otter.models.SettingsStore
import tel.jeelpa.otter.models.TrackerStoreImpl
import tel.jeelpa.otter.models.UserStore
import tel.jeelpa.otter.plugins.TrackerManager
import tel.jeelpa.otter.plugins.TrackerStore
import tel.jeelpa.otter.triggers.RefreshTrigger
import tel.jeelpa.otter.ui.fragments.mediaCommon.MediaEditorBottomSheetFactory
import tel.jeelpa.plugininterface.storage.UserStorage
import tel.jeelpa.plugininterface.tracker.repository.AnimeClient
import tel.jeelpa.plugininterface.tracker.repository.CharacterClient
import tel.jeelpa.plugininterface.tracker.repository.ClientHolder
import tel.jeelpa.plugininterface.tracker.repository.MangaClient
import tel.jeelpa.plugininterface.tracker.repository.UserClient
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
    fun providesSettingsOfApp(application: Application) : SettingsStore {
        return SettingsStore(application)
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
@InstallIn(SingletonComponent::class)
class UserClientSingletonModule {
    @Provides
    @Singleton
    //Private
    fun providesUserClient(clientHolder: ClientHolder): UserClient =
        clientHolder.userClient

    @Provides
    @Singleton
    fun providesMediaEditorBottomSheetFactory(
        client: UserClient,
        @Named("UserDataRefreshTrigger") userDataRefreshTrigger: RefreshTrigger
    ) = MediaEditorBottomSheetFactory(client, userDataRefreshTrigger)

}

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelScopeClients {

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

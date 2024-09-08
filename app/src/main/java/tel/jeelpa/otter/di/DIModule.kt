package tel.jeelpa.otter.di

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import tel.jeelpa.anilisttrackerplugin.data.ClientHolderImpl
import tel.jeelpa.aniyomi_runtime.AniyomiPluginRepo
import tel.jeelpa.otter.models.SettingsStore
import tel.jeelpa.otter.models.TrackerStore
import tel.jeelpa.otter.models.TrackerStoreImpl
import tel.jeelpa.otter.models.UserStore
import tel.jeelpa.otter.plugins.OtterPluginManifestParser
import tel.jeelpa.otter.plugins.OtterPluginTrackerLoader
import tel.jeelpa.otter.plugins.PluginManifest
import tel.jeelpa.otter.plugins.adapters.ClientHolderAdapter
import tel.jeelpa.otter.triggers.RefreshTrigger
import tel.jeelpa.otter.ui.fragments.mediaCommon.MediaEditorBottomSheetFactory
import tel.jeelpa.plugger.PluginRepo
import tel.jeelpa.plugger.PluginRepoImpl
import tel.jeelpa.plugger.pluginloader.apk.ApkPluginSource
import tel.jeelpa.plugger.pluginloader.file.FileSystemPluginSource
import tel.jeelpa.plugininterface.storage.UserStorage
import tel.jeelpa.plugininterface.tracker.repository.AnimeClient
import tel.jeelpa.plugininterface.tracker.repository.CharacterClient
import tel.jeelpa.plugininterface.tracker.repository.ClientHolder
import tel.jeelpa.plugininterface.tracker.repository.MangaClient
import tel.jeelpa.plugininterface.tracker.repository.UserClient
import javax.inject.Named
import javax.inject.Singleton

//@JvmInline
data class CurrentTrackerNameFlow(val value: Flow<String?>)

//@JvmInline
//class CurrentTrackerClient(val value: Flow<ClientHolder?>)

//class UserClientFlow(val value: Flow<UserClient>)

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
    fun providesCurrentTrackerName(
        trackerStore: TrackerStore,
    ): CurrentTrackerNameFlow =
        CurrentTrackerNameFlow(trackerStore.getTracker())

    @RequiresApi(Build.VERSION_CODES.Q)
    @Provides
    @Singleton
    fun providesTrackerRepo(
        application: Application,
        userStorage: UserStorage,
        httpClient: OkHttpClient,
    ): PluginRepo<PluginManifest, ClientHolder> {
        val source = FileSystemPluginSource(application.filesDir.also { println("FILE : ${it.path}") }, ".jar")
        val parser = OtterPluginManifestParser(application)
        val loader = OtterPluginTrackerLoader(
            application,
            userStorage,
            httpClient
        )

        return PluginRepoImpl(
            pluginSource = source,
            manifestParser = parser,
            pluginLoader = loader,
        )
    }

    @Provides
    @Singleton
    //Private
    fun providesClientHolder(
        userStorage: UserStorage,
        //
        trackerName: CurrentTrackerNameFlow,
        trackerPluginRepo: PluginRepo<PluginManifest, ClientHolder>
    ): ClientHolder {
        val trackerFlow = trackerPluginRepo.getAllPlugins()
        val nameFlow = trackerName.value

//        val clientHolderFlow = combine(trackerFlow, nameFlow) { trackers, className ->
//            println("trackers $trackers")
//            println("className $className")
//            trackers.first().getOrThrow()
//                mapNotNull { it.getOrNull() }
//                .find { it.first.className == className }!!.also { println("FOUND ${it.first}") }
//                .second
//        }
        val clientHolderFlow = MutableStateFlow(ClientHolderImpl(userStorage))
        return ClientHolderAdapter(clientHolderFlow)
    }

    @Provides
    @Singleton
    fun providesAniyomiPlugins(
        application: Application
    ): AniyomiPluginRepo {
        return AniyomiPluginRepo(application)
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

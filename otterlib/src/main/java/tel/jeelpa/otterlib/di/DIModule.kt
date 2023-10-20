package tel.jeelpa.otterlib.di

import android.app.Application
import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import tel.jeelpa.otterlib.data.AnimeClientImpl
import tel.jeelpa.otterlib.data.LoginImpl
import tel.jeelpa.otterlib.data.MangaClientImpl
import tel.jeelpa.otterlib.data.TrackerClientImpl
import tel.jeelpa.otterlib.models.ANILIST_DATA
import tel.jeelpa.otterlib.repository.AnimeClient
import tel.jeelpa.otterlib.repository.LoginProcedure
import tel.jeelpa.otterlib.repository.MangaClient
import tel.jeelpa.otterlib.repository.TrackerClient
import tel.jeelpa.otterlib.store.UserStore
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DILibModule {

    @Provides
    @Singleton
    fun providesAnilistApolloClient() : ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://graphql.anilist.co/")
            .build()
    }

    @Provides
    @Singleton
    fun providesAnilistAnimeClientImpl(apolloClient: ApolloClient) : AnimeClient {
        return AnimeClientImpl(apolloClient)
    }

    @Provides
    @Singleton
    fun providesAnilistMangaClientImpl(apolloClient: ApolloClient) : MangaClient {
        return MangaClientImpl(apolloClient)
    }

    @Provides
    @Singleton
    fun providesLoginProcedureUseCase(
        application: Application
    ): LoginProcedure {
        return LoginImpl(
            application,
            ANILIST_DATA.id,
            ANILIST_DATA.redirectUri
        )
    }

    @Provides
    @Singleton
    fun providesUserStore(application: Application) : UserStore {
        return UserStore(application)
    }

    @Provides
    @Singleton
    fun providesTrackerClient(
        httpClient: OkHttpClient,
        userStore: UserStore,
    ) : TrackerClient {
        return TrackerClientImpl(
            httpClient,
            userStore,
        )
    }
}
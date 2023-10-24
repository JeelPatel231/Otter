package tel.jeelpa.otterlib.di

import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tel.jeelpa.otterlib.data.AnimeClientImpl
import tel.jeelpa.otterlib.data.MangaClientImpl
import tel.jeelpa.otterlib.repository.AnimeClient
import tel.jeelpa.otterlib.repository.MangaClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DiLibModule {

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
}
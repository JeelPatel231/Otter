package tel.jeelpa.otterlib.di

import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import tel.jeelpa.otterlib.data.AnimeClientImpl
import tel.jeelpa.otterlib.data.CharacterClientImpl
import tel.jeelpa.otterlib.data.MangaClientImpl
import tel.jeelpa.otterlib.repository.AnimeClient
import tel.jeelpa.otterlib.repository.CharacterClient
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

}

// TODO : Scope this better
@Module
@InstallIn(ViewModelComponent::class)
class DIViewModelModule {

    @Provides
    @ViewModelScoped
    fun providesAnilistAnimeClientImpl(apolloClient: ApolloClient) : AnimeClient {
        return AnimeClientImpl(apolloClient)
    }

    @Provides
    @ViewModelScoped
    fun providesAnilistMangaClientImpl(apolloClient: ApolloClient) : MangaClient {
        return MangaClientImpl(apolloClient)
    }


    @Provides
    @ViewModelScoped
    fun providesAnilistCharacterClientImpl(apolloClient: ApolloClient) : CharacterClient {
        return CharacterClientImpl(apolloClient)
    }

}
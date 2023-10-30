
import okhttp3.OkHttpClient
import tel.jeelpa.otter.reference.RegisterUseCase
import tel.jeelpa.plugin.extractors.AllAnimeDirectExtractor
import tel.jeelpa.plugin.extractors.AllAnimeExtractor
import tel.jeelpa.plugin.extractors.AnimePaheExtractor
import tel.jeelpa.plugin.extractors.DummyExtractor
import tel.jeelpa.plugin.extractors.StreamTape
import tel.jeelpa.plugin.extractors.VidStreaming
import tel.jeelpa.plugin.parsers.AllAnime
import tel.jeelpa.plugin.parsers.AnimePahe
import tel.jeelpa.plugin.parsers.DummyParser
import tel.jeelpa.plugin.parsers.Kaido

class Metadata(
    okHttpClient: OkHttpClient,
    registerUseCase: RegisterUseCase,
) {
    init {
        // register your parsers like this
        registerUseCase.registerParser(
            DummyParser(),
            AnimePahe(okHttpClient),
            Kaido(okHttpClient),
            AllAnime(okHttpClient)
        )

        // register your extractors here
        registerUseCase.registerExtractor(
            DummyExtractor(),
            StreamTape(okHttpClient),
            VidStreaming(okHttpClient),
            AnimePaheExtractor(okHttpClient),
            AllAnimeExtractor(okHttpClient),
            AllAnimeDirectExtractor(),
        )
    }
}
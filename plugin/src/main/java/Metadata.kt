
import okhttp3.OkHttpClient
import tel.jeelpa.otter.reference.RegisterExtractorUseCase
import tel.jeelpa.otter.reference.RegisterParserUseCase
import tel.jeelpa.plugin.extractors.AllAnimeDirectExtractor
import tel.jeelpa.plugin.extractors.AllAnimeExtractor
import tel.jeelpa.plugin.extractors.AnimePaheExtractor
import tel.jeelpa.plugin.extractors.DummyExtractor
import tel.jeelpa.plugin.extractors.FileMoon
import tel.jeelpa.plugin.extractors.GogoCDN
import tel.jeelpa.plugin.extractors.RapidCloud
import tel.jeelpa.plugin.extractors.StreamTape
import tel.jeelpa.plugin.extractors.VidStreaming
import tel.jeelpa.plugin.parsers.AllAnime
import tel.jeelpa.plugin.parsers.AnimePahe
import tel.jeelpa.plugin.parsers.DummyParser
import tel.jeelpa.plugin.parsers.Kaido

class ParserMetadata(
    okHttpClient: OkHttpClient,
    registerParser: RegisterParserUseCase,
) {
    init {
        // register your parsers like this
        registerParser(
            DummyParser(),
            AllAnime(okHttpClient),
            AnimePahe(okHttpClient),
            Kaido(okHttpClient)
        )
    }
}


class ExtractorMetadata(
    okHttpClient: OkHttpClient,
    registerExtractor: RegisterExtractorUseCase
){
    init {
        // register your extractors here
        registerExtractor(
            DummyExtractor(),
            AllAnimeExtractor(okHttpClient),
            AllAnimeDirectExtractor(),
            AnimePaheExtractor(okHttpClient),
            FileMoon(okHttpClient),
            GogoCDN(okHttpClient),
            RapidCloud(okHttpClient),
            StreamTape(okHttpClient),
            VidStreaming(okHttpClient)
        )
    }
}
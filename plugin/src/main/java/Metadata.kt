
import okhttp3.OkHttpClient
import tel.jeelpa.otter.reference.RegisterExtractorUseCase
import tel.jeelpa.otter.reference.RegisterParserUseCase
import tel.jeelpa.plugin.extractors.DummyExtractor
import tel.jeelpa.plugin.parsers.DummyParser

class ParserMetadata(
    okHttpClient: OkHttpClient,
    registerParser: RegisterParserUseCase,
) {
    init {
        // register your parsers like this
        registerParser(
            DummyParser(),
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
        )
    }
}

import okhttp3.OkHttpClient
import tel.jeelpa.otter.reference.RegisterUseCase
import tel.jeelpa.plugin.extractors.DummyExtractor
import tel.jeelpa.plugin.parsers.DummyParser

class Metadata(
    okHttpClient: OkHttpClient,
    registerUseCase: RegisterUseCase,
) {
    init {
        // register your parsers like this
        registerUseCase.registerParser(
            DummyParser(),
        )

        // register your extractors here
        registerUseCase.registerExtractor(
            DummyExtractor(),
        )
    }
}
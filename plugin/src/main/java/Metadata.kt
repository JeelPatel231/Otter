import okhttp3.OkHttpClient
import tel.jeelpa.otter.reference.PluginMetadata
import tel.jeelpa.otter.reference.RegisterUseCase
import tel.jeelpa.plugin.AnimePahe
import tel.jeelpa.plugin.AnimePaheExtractor
import tel.jeelpa.plugin.DummyExtractor
import tel.jeelpa.plugin.DummyParser

class Metadata(
    okHttpClient: OkHttpClient,
    registerUseCase: RegisterUseCase,
) : PluginMetadata {
    override val plugins = listOf(
        DummyParser(okHttpClient, registerUseCase),
        DummyExtractor(okHttpClient, registerUseCase),
        AnimePaheExtractor(okHttpClient, registerUseCase),
        AnimePahe(okHttpClient, registerUseCase)
    )
}
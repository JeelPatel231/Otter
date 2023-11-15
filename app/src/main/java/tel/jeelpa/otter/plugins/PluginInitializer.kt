package tel.jeelpa.otter.plugins

import android.content.Context
import dalvik.system.DexClassLoader
import okhttp3.OkHttpClient
import tel.jeelpa.otter.reference.RegisterExtractorUseCase
import tel.jeelpa.otter.reference.RegisterParserUseCase
import tel.jeelpa.otter.trackerinterface.RegisterTrackerUseCase
import tel.jeelpa.otter.trackerinterface.repository.UserStorage
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

class PluginInitializer (
    private val context: Context,
    private val httpClient: OkHttpClient,
    private val extractorUseCase: RegisterExtractorUseCase,
    private val parserUseCase: RegisterParserUseCase,
    private val trackerUseCase: RegisterTrackerUseCase,
    private val userStorage: UserStorage
) {

    enum class MetadataClassNames {
        ParserMetadata,
        ExtractorMetadata,
        TrackerMetadata,
    }

    private fun getNamedClassConstructor(name: MetadataClassNames, classLoader: DexClassLoader): KFunction<Any>? {
        return try {
            Class.forName(name.name, true, classLoader)
                .kotlin
                .primaryConstructor!!
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    operator fun invoke(pathToJar: String) {
        val classLoader = DexClassLoader(
            pathToJar,
            context.codeCacheDir.absolutePath, // can be null, but crashes with NPE on Nougat
            null,
            context.classLoader
        )

        getNamedClassConstructor(MetadataClassNames.ParserMetadata, classLoader)
            ?.call(httpClient, parserUseCase)

        getNamedClassConstructor(MetadataClassNames.ExtractorMetadata, classLoader)
            ?.call(httpClient, extractorUseCase)

        getNamedClassConstructor(MetadataClassNames.TrackerMetadata, classLoader)
            ?.call(userStorage, trackerUseCase)
    }
}

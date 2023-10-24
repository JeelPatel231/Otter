package tel.jeelpa.otter.plugins

import android.content.Context
import dalvik.system.DexClassLoader
import okhttp3.OkHttpClient
import tel.jeelpa.otter.reference.PluginMetadata
import tel.jeelpa.otter.reference.RegisterUseCase
import kotlin.reflect.full.primaryConstructor

class PluginInitializer (
    private val context: Context,
    private val httpClient: OkHttpClient,
    private val registerUseCase: RegisterUseCase,
) {

    operator fun invoke(pathToJar: String) {
        val classLoader = DexClassLoader(
            pathToJar,
            context.codeCacheDir.absolutePath,
            null,
            context.classLoader
        )

        Class.forName("Metadata", true, classLoader)
            .kotlin
            .primaryConstructor!!
            .call(httpClient, registerUseCase)
            .let { it as PluginMetadata }
            .plugins.forEach { it.register() }
    }
}

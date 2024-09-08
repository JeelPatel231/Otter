package tel.jeelpa.otter.plugins

import  android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import tel.jeelpa.plugger.ManifestParser
import tel.jeelpa.plugger.PluginLoader
import tel.jeelpa.plugger.models.PluginMetadata
import tel.jeelpa.plugger.pluginloader.GetClassLoaderWithPathUseCase
import tel.jeelpa.plugininterface.storage.UserStorage
import tel.jeelpa.plugininterface.tracker.repository.ClientHolder
import java.io.File

@Serializable
enum class PluginType {
    PARSER, EXTRACTOR, TRACKER
}

@Serializable
data class PluginManifest(
    override val path: String,
    override val className: String,
    val name: String,
    val type: PluginType,
) : PluginMetadata

@Serializable
data class JsonPluginManifest(
    val className: String,
    val name: String,
    val type: PluginType,
) {
    fun toPluginManifest(path: String) = PluginManifest(path, className, name, type)
}

//class OtterPluginManifestParser: ManifestParser<ApplicationInfo, PluginManifest> {
//    companion object {
//        const val PREFIX = "tel.jeelpa.otter.plugin"
//        const val SOURCE_CLASS_TAG = "$PREFIX.class"
//        const val PLUGIN_TYPE_TAG = "$PREFIX.type"
//    }
//    override fun parseManifest(data: ApplicationInfo): PluginManifest {
//        return PluginManifest(
//            name = data.name ?: "NO NAME",
//            className = data.metaData.getString(SOURCE_CLASS_TAG)
//                ?: error("ClassName not found in Metadata"),
//            type = PluginType.valueOf(
//                data.metaData.getString(PLUGIN_TYPE_TAG)?.uppercase()
//                    ?: error("Plugin Type not found in Metadata")
//            ),
//            path = data.sourceDir // TODO: does this work for both apk file and installed apps?
//        )
//    }
//}
class OtterPluginManifestParser(
    private val getClassLoader: GetClassLoaderWithPathUseCase
) : ManifestParser<File, PluginManifest> {
    constructor(context: Context) : this(GetClassLoaderWithPathUseCase(context.classLoader))

    override fun parseManifest(data: File): PluginManifest {
        val manifestText =
            getClassLoader.getWithPath(data.path).getResourceAsStream("manifest.json").readBytes()
                .toString(Charsets.UTF_8)

        return Json.decodeFromString<JsonPluginManifest>(manifestText).toPluginManifest(data.path)
    }
}

class OtterPluginTrackerLoader(
    private val getClassLoader: GetClassLoaderWithPathUseCase,
    private val userStorage: UserStorage,
    private val okHttpClient: OkHttpClient,
) : PluginLoader<PluginManifest, ClientHolder> {

    constructor(context: Context, userStorage: UserStorage, okHttpClient: OkHttpClient) : this(
        GetClassLoaderWithPathUseCase(context.classLoader),
        userStorage,
        okHttpClient
    )

    override fun loadPlugin(pluginMetadata: PluginManifest): ClientHolder {
        return getClassLoader.getWithPath(pluginMetadata.path)
            .loadClass(pluginMetadata.className).constructors.first()
            // TODO: create a class for dependencies
            .newInstance(userStorage, okHttpClient) as ClientHolder
    }
}

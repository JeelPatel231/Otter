package tel.jeelpa.otter.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.ActivityPluginDownloadBinding
import tel.jeelpa.otter.ui.generic.showToast
import java.io.File


internal interface ProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}

@AndroidEntryPoint
class PluginDeepLinkHandlerActivity : AppCompatActivity() {
    private var _binding: ActivityPluginDownloadBinding? = null
    private val binding get() = _binding!!

    private fun exitToast(text: String) {
        showToast(text)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPluginDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jarLink = intent?.data?.path?.removePrefix("/")
            ?: return exitToast("Intent Data Null/Invalid!")

        // TODO : get the filename from Content-Disposition Header
        var jarName = jarLink.substringAfterLast('/').substringBefore('?')
        val request = Request.Builder().url(jarLink).build()

        val progressListener = object : ProgressListener {
            var firstUpdate = true
            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                if (done) {
                    showToast("Plugin Added")
                    val settingIntent =
                        Intent(this@PluginDeepLinkHandlerActivity, SettingsActivity::class.java)
                    startActivity(settingIntent)
                    return finish()
                }

                if (firstUpdate) {
                    firstUpdate = false
                    binding.downloadProgress.isIndeterminate = contentLength == -1L
                }
                if (contentLength != -1L) {
                    val prog = 100 * bytesRead / contentLength
                    binding.downloadProgress.setProgress(prog.toInt(), true)
                    binding.bytesHolder.text =
                        getString(R.string.bytes_downloaded, (bytesRead / 1000).toString())
                }
            }
        }

        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(Interceptor { chain ->
                val resp = chain.proceed(chain.request())
                resp.newBuilder().body(ProgressResponseBody(resp.body, progressListener))
                    .build()
            }).build()

        val downloadJob = downloadJob@{
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@downloadJob exitToast("Request Failed!")
                }
                val filePath = File(getExternalFilesDir("plugins"), jarName)
                response.body?.byteStream()?.copyTo(filePath.outputStream())
                    ?: return@downloadJob exitToast("Body content was null")
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setMessage("Filename doesnt end with .jar, do you want to continue? This will append .jar to the file name and save it")
            .setPositiveButton("OK") { _, _ ->
                jarName += ".jar"
                lifecycleScope.launch(Dispatchers.IO) { downloadJob() }
            }
            .setNegativeButton("Cancel") { _, _ ->
                exitToast("Download Cancelled.")
            }

        if (!jarName.endsWith(".jar")) {
            dialog.show()
        } else {
            lifecycleScope.launch(Dispatchers.IO) { downloadJob() }
        }

    }

}

private class ProgressResponseBody(
    private val responseBody: ResponseBody?,
    private val progressListener: ProgressListener
) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null
    override fun contentType(): MediaType? {
        return responseBody!!.contentType()
    }

    override fun contentLength(): Long {
        return responseBody!!.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody!!.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                progressListener.update(
                    totalBytesRead,
                    responseBody!!.contentLength(),
                    bytesRead == -1L
                )
                return bytesRead
            }
        }
    }
}

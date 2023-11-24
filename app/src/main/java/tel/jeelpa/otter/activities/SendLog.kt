package tel.jeelpa.otter.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tel.jeelpa.otter.databinding.ActivitySendLogBinding

class SendLog : AppCompatActivity() {

    private var _binding: ActivitySendLogBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySendLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val stackTrace = intent.getStringExtra(Intent.EXTRA_TEXT) ?: "Bruh, wat?"
        binding.stackTrace.text = stackTrace

        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, stackTrace)
            }
            startActivity(Intent.createChooser(intent, "Share Crash Logs"))
        }

    }
}
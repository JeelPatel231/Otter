package tel.jeelpa.otter

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import tel.jeelpa.otter.databinding.ActivityOnboardingBinding
import tel.jeelpa.otter.trackerinterface.TrackerManager
import tel.jeelpa.otter.trackerinterface.TrackerStore
import javax.inject.Inject


@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    val trackerStore: TrackerStore,
    val trackerManager: TrackerManager,
) : ViewModel()

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {
    private var _binding: ActivityOnboardingBinding? = null
    private val binding get() = _binding!!

    private val onBoardingVM: OnBoardingViewModel by viewModels()

    private fun proceedToMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // don't do the view rendering if everything is already set up
        runBlocking {
            if (onBoardingVM.trackerStore.getTracker().first() != null)
                proceedToMain()
        }

        _binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trackerSelector.setOnCheckedChangeListener { _, _ ->
            binding.proceedBtn.isEnabled = true
        }
        val btnLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        onBoardingVM.trackerManager.trackers
            .map { it.uniqueId }
            .map { RadioButton(this).apply {
                text = it
                layoutParams = btnLayoutParams
            } }
            .forEach { binding.trackerSelector.addView(it) }

        binding.proceedBtn.setOnClickListener {
            val selectedRadioBtnId = binding.trackerSelector.checkedRadioButtonId
            val trackerId = findViewById<RadioButton>(selectedRadioBtnId).text.toString()
            runBlocking { onBoardingVM.trackerStore.saveTracker(trackerId) }
            proceedToMain()
        }
    }
}
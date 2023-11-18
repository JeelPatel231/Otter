package tel.jeelpa.otter

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.VISIBLE
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import tel.jeelpa.otter.databinding.ActivityOnboardingBinding
import tel.jeelpa.otter.trackerinterface.TrackerManager
import tel.jeelpa.otter.trackerinterface.TrackerStore
import tel.jeelpa.otter.trackerinterface.repository.UserStorage
import javax.inject.Inject


@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    val trackerStore: TrackerStore,
    val trackerManager: TrackerManager,
    val userStorage: UserStorage,
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

    private suspend fun checkTrackerHealth(): Boolean {
        // Tracker Store has ID, but the class wasnt loaded
        val registeredId = onBoardingVM.trackerStore.getTracker().firstOrNull()
            ?: return false
        return try {
            // throws exception when not found
            onBoardingVM.trackerManager.getTracker(registeredId)
            true
        } catch (e: Throwable) {
            // clear the set tracker and user data to mitigate any inconsistencies on changing clients
            onBoardingVM.trackerStore.clearTracker()
            onBoardingVM.userStorage.clearData()
            binding.trackerHealthError.apply {
                text = "ERROR: Could not load your registered tracker. User Data was Cleared, reconfigure the app to proceed."
                visibility = VISIBLE
            }
            false
        }
    }

    private val healthChecks: Array<suspend  () -> Boolean> =
        arrayOf(::checkTrackerHealth)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnboardingBinding.inflate(layoutInflater)

        // don't do the view rendering if everything is already set up
        runBlocking {
            if(healthChecks.all { it() }){
                proceedToMain()
            }
        }

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
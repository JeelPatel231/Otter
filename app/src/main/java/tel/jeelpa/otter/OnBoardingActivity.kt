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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import tel.jeelpa.otter.databinding.ActivityOnboardingBinding
import tel.jeelpa.otter.di.CurrentTrackerNameFlow
import tel.jeelpa.otter.models.TrackerStore
import tel.jeelpa.otter.plugins.PluginManifest
import tel.jeelpa.otter.ui.generic.observeUntil
import tel.jeelpa.plugger.PluginRepo
import tel.jeelpa.plugininterface.storage.UserStorage
import tel.jeelpa.plugininterface.tracker.repository.ClientHolder
import tel.jeelpa.plugininterface.tracker.repository.UserClient
import javax.inject.Inject


@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    val trackerName: CurrentTrackerNameFlow,
    val trackerClient: UserClient,
    val trackerRepo: PluginRepo<PluginManifest, ClientHolder>,
    val trackerStore: TrackerStore,
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
        return true;
        // return if no name is stored
        val preferredClientName = onBoardingVM.trackerName.value.firstOrNull()
            ?: return false

        // return if name is stored but class not loaded
        val client = onBoardingVM.trackerClient
            .also { println("CLIENT: $it") }
            ?: return false

        return true
        // clear the set tracker and user data to mitigate any inconsistencies on changing clients
//        onBoardingVM.trackerStore.clearTracker()
//        onBoardingVM.userStorage.clearData()
//        binding.trackerHealthError.apply {
//            text = "ERROR: Could not load your registered tracker. User Data was Cleared, reconfigure the app to proceed."
//            visibility = VISIBLE
//        }
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

        onBoardingVM.trackerRepo.getAllPlugins().observeUntil(this, { true }) { pluginList ->
        pluginList.mapNotNull { it.getOrNull()?.first }
            .map { it.className }
            .map {
                RadioButton(this).apply {
                    text = it
                    layoutParams = btnLayoutParams
                }
            }
            .forEach { binding.trackerSelector.addView(it) }
        }

        binding.proceedBtn.setOnClickListener {
            val selectedRadioBtnId = binding.trackerSelector.checkedRadioButtonId
            val trackerId = findViewById<RadioButton>(selectedRadioBtnId).text.toString()
            runBlocking { onBoardingVM.trackerStore.saveTracker(trackerId) }
            proceedToMain()
        }
    }
}
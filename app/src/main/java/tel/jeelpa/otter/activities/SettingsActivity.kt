package tel.jeelpa.otter.activities

import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tel.jeelpa.otter.OnBoardingActivity
import tel.jeelpa.otter.databinding.ActivitySettingsBinding
import tel.jeelpa.otter.ui.generic.MaterialSpinnerAdapter
import tel.jeelpa.otter.ui.generic.observeUntil
import tel.jeelpa.otter.ui.generic.restartApp
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.plugininterface.storage.UserStorage
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val trackerStore: tel.jeelpa.plugininterface.tracker.TrackerStore,
    private val trackerManager: tel.jeelpa.plugininterface.tracker.TrackerManager,
    private val userStorage: UserStorage,
) : ViewModel() {
    val trackers
        get() = trackerManager.trackers
}


@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModels()

    private fun showConfirmDialogWithText(body: String, onAccept: (DialogInterface, Int) -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage(body)
            .setPositiveButton("OK", DialogInterface.OnClickListener(onAccept))
            .setNegativeButton("Cancel", null)
            .show()
    }

    private suspend fun checkTrackerService(): Boolean {
        val newValue = binding.trackerServiceSelector.text.toString()
        val oldValue = settingsViewModel.trackerStore.getTracker().first()
        if (newValue != oldValue){
            settingsViewModel.trackerStore.saveTracker(newValue)
            return true
        }
        return false
    }

    private val listOfChecks: Array<suspend () -> Boolean> =
        arrayOf(::checkTrackerService)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set the Spinner text to current tracker
        settingsViewModel.trackerStore.getTracker().observeUntil(this, { true }) {
            binding.trackerServiceSelector.setText(it!!)
        }

        binding.trackerServiceSelector.apply {
            setAdapter(
                MaterialSpinnerAdapter(
                    this@SettingsActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    settingsViewModel.trackers
                )
            )
        }

        binding.applyBtn.setOnClickListener {
            lifecycleScope.launch {
                // if ATLEAST ONE value has been changed, ask to restart the app
                if(listOfChecks.any { it() }){
                    showConfirmDialogWithText("Restart App for changes to take effect.") { _, _ ->
                        finishAffinity()
                        restartApp(OnBoardingActivity::class.java)
                    }
                } else {
                    showToast("No Changes were made.")
                }
            }
        }

    }
}
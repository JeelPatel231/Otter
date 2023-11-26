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
import tel.jeelpa.otter.plugins.TrackerManager
import tel.jeelpa.otter.plugins.TrackerStore
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val trackerStore: TrackerStore,
    private val trackerManager: TrackerManager,
    val userStorage: UserStorage,
) : ViewModel() {
    val trackers
        get() = trackerManager.trackers
}


@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModels()

    companion object {
        private const val SELECT_TRACKER = "Select a Tracker"
    }

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

        // if not tracker is selected and if its the default text
        if (newValue == SELECT_TRACKER) return false
        // if value didn't change
        if (newValue == oldValue) return false

        settingsViewModel.trackerStore.saveTracker(newValue)
        settingsViewModel.userStorage.clearData()
        return true
    }

    private val listOfChecks: Array<suspend () -> Boolean> =
        arrayOf(::checkTrackerService)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set the Spinner text to current tracker
        settingsViewModel.trackerStore.getTracker().observeUntil(this, { true }) {
            when (it) {
                null -> binding.trackerServiceSelector.setText(SELECT_TRACKER)
                else -> binding.trackerServiceSelector.setText(it)
            }
        }

        binding.trackerServiceSelector.apply {
            setAdapter(
                MaterialSpinnerAdapter(
                    this@SettingsActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    settingsViewModel.trackers.toList()
                )
            )
        }

        binding.applyBtn.setOnClickListener {
            lifecycleScope.launch {
                // if ATLEAST ONE value has been changed, ask to restart the app
                if (listOfChecks.any { it() }) {
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
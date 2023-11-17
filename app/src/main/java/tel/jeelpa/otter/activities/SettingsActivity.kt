package tel.jeelpa.otter.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tel.jeelpa.otter.OnBoardingActivity
import tel.jeelpa.otter.databinding.ActivitySettingsBinding
import tel.jeelpa.otter.trackerinterface.TrackerManager
import tel.jeelpa.otter.trackerinterface.TrackerStore
import tel.jeelpa.otter.trackerinterface.repository.ClientHolder
import tel.jeelpa.otter.trackerinterface.repository.UserStorage
import tel.jeelpa.otter.ui.generic.MaterialSpinnerAdapter
import tel.jeelpa.otter.ui.generic.observeUntil
import tel.jeelpa.otter.ui.generic.restartApp
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val trackerStore: TrackerStore,
    private val trackerManager: TrackerManager,
    private val userStorage: UserStorage,
) : ViewModel() {
    val trackers
        get() = trackerManager.trackers

    suspend fun changeTracker(uniqueId: String): Boolean {
        val currentTrackerId = trackerStore.getTracker().first()
        if (currentTrackerId == uniqueId) return false
        trackerStore.saveTracker(uniqueId)
        userStorage.clearData()
        return true
    }
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

            setOnItemClickListener { adapterView, _, idx, _ ->
                val client = adapterView.getItemAtPosition(idx) as ClientHolder
                lifecycleScope.launch {
                    if(settingsViewModel.changeTracker(client.uniqueId)) {
                        showConfirmDialogWithText("Restart App for changes to take effect?") { _, _ ->
                            finishAffinity()
                            restartApp(OnBoardingActivity::class.java)
                        }
                    }
                }
            }
        }
    }
}
package tel.jeelpa.otter.ui.fragments.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tel.jeelpa.otter.databinding.NoLoginLayoutBinding
import tel.jeelpa.otter.trackerinterface.TrackerManager
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.showToast
import javax.inject.Inject


@AndroidEntryPoint
class NoLoginFragment: Fragment() {

    private var binding: NoLoginLayoutBinding by autoCleared()
    @Inject lateinit var trackerManager: TrackerManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NoLoginLayoutBinding.inflate(inflater, container, false)
        binding.loginButton.setOnClickListener {
            lifecycleScope.launch {
                val currentTracker = trackerManager.getCurrentTracker().first()
                    ?: throw Exception("No Tracker Selected as Default")

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(currentTracker.userClient.loginUri)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                startActivity(intent)
            }
        }

        binding.pluginsButton.setOnClickListener {
            showToast("Not Implemented")
        }

        return binding.root
    }
}
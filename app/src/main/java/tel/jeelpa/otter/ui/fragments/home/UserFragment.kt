package tel.jeelpa.otter.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tel.jeelpa.otter.databinding.FragmentUserBinding
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.otterlib.repository.TrackerClient
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment: Fragment() {

    private var binding: FragmentUserBinding by autoCleared()
    @Inject lateinit var trackerClient: TrackerClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                trackerClient.logout()
            }
        }

        lifecycleScope.launch {
            binding.username.text = trackerClient.getUser().username
        }

        binding.pluginsButton.setOnClickListener {
            showToast("Not Implemented")
//            getOuterNavController().navigate(MainFragmentDirections.toPluginFragment())
        }
        return binding.root
    }
}
package tel.jeelpa.otter.ui.fragments.home

import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tel.jeelpa.otter.databinding.FragmentUserBinding
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.otterlib.repository.TrackerClient
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment: ViewBindingFragment<FragmentUserBinding>(FragmentUserBinding::inflate) {

    @Inject lateinit var trackerClient: TrackerClient

    override fun onCreateBindingView() {
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

    }
}
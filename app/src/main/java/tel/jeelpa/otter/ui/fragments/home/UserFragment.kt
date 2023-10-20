package tel.jeelpa.otter.ui.fragments.home

import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tel.jeelpa.otter.databinding.FragmentUserBinding
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.getOuterNavController
import tel.jeelpa.otter.ui.generic.showToast
import javax.inject.Inject

//@AndroidEntryPoint
class UserFragment: ViewBindingFragment<FragmentUserBinding>(FragmentUserBinding::inflate) {
    override fun onCreateBindingView() {
        binding.logoutButton.setOnClickListener {
            showToast("Not Implemented")
//            lifecycleScope.launch { trackerUseCases.logoutUserUseCase() }
        }

        binding.pluginsButton.setOnClickListener {
            showToast("Not Implemented")
//            getOuterNavController().navigate(MainFragmentDirections.toPluginFragment())
        }

    }
}
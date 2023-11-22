package tel.jeelpa.otter.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import tel.jeelpa.otter.databinding.FragmentHomeBinding
import tel.jeelpa.otter.models.UserClientHolderModel
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.observeFlowFlex

@AndroidEntryPoint
class HomeContainerFragment : Fragment() {
    private var binding: FragmentHomeBinding by autoCleared()
    private val viewModel: UserClientHolderModel by viewModels()

    private fun navigateTo(fragmentInstance: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(binding.homeFragmentContainerView.id, fragmentInstance)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel.loggedIn.observeFlowFlex(viewLifecycleOwner) {
            distinctUntilChanged().collectLatest {
                when (it) {
                    true -> navigateTo(UserFragment())
                    false -> navigateTo(NoLoginFragment())
                }
            }
        }

        return binding.root
    }
}
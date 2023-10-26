package tel.jeelpa.otter.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.NoLoginLayoutBinding
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.otterlib.repository.LoginProcedure
import javax.inject.Inject


@AndroidEntryPoint
class NoLoginFragment: Fragment() {

    private var binding: NoLoginLayoutBinding by autoCleared()
    @Inject lateinit var loginUseCase: LoginProcedure

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NoLoginLayoutBinding.inflate(inflater, container, false)
        binding.loginButton.setOnClickListener {
            loginUseCase()
        }

        binding.pluginsButton.setOnClickListener {
            showToast("Not Implemented")
        }

        return binding.root
    }
}
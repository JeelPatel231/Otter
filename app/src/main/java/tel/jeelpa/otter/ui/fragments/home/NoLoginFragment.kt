package tel.jeelpa.otter.ui.fragments.home

import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.NoLoginLayoutBinding
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.otterlib.repository.LoginProcedure
import javax.inject.Inject


@AndroidEntryPoint
class NoLoginFragment: ViewBindingFragment<NoLoginLayoutBinding>(NoLoginLayoutBinding::inflate) {

    @Inject lateinit var loginUseCase: LoginProcedure

    override fun onCreateBindingView() {
        binding.loginButton.setOnClickListener {
            loginUseCase()
        }

        binding.pluginsButton.setOnClickListener {
            showToast("Not Implemented")
        }
    }
}
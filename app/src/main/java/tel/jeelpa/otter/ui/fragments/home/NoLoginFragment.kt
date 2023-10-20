package tel.jeelpa.otter.ui.fragments.home

import tel.jeelpa.otter.databinding.NoLoginLayoutBinding
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.showToast

class NoLoginFragment: ViewBindingFragment<NoLoginLayoutBinding>(NoLoginLayoutBinding::inflate) {

    override fun onCreateBindingView() {
        binding.loginButton.setOnClickListener {
            showToast("Not Implemented")
        }

        binding.pluginsButton.setOnClickListener {
            showToast("Not Implemented")
        }
    }
}
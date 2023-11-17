package tel.jeelpa.otter.ui.fragments.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.activities.SettingsActivity
import tel.jeelpa.otter.databinding.NoLoginLayoutBinding
import tel.jeelpa.otter.models.UserClientHolderModel
import tel.jeelpa.otter.ui.generic.autoCleared


@AndroidEntryPoint
class NoLoginFragment: Fragment() {

    private var binding: NoLoginLayoutBinding by autoCleared()
    private val userClientHolderModel: UserClientHolderModel by viewModels()
    private val userClient
        get() = userClientHolderModel.userClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NoLoginLayoutBinding.inflate(inflater, container, false)

        binding.avatarHolder.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(userClient.loginUri)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            startActivity(intent)
        }

        return binding.root
    }
}
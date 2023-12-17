package tel.jeelpa.otter.ui.fragments.mediaCommon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tel.jeelpa.otter.databinding.MediaUpdateBottomSheetBinding
import tel.jeelpa.otter.triggers.RefreshTrigger
import tel.jeelpa.otter.ui.generic.InputFilterMinMax
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.plugininterface.tracker.models.AppMediaListStatus
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.UserMediaAnime
import tel.jeelpa.plugininterface.tracker.repository.UserClient


class MediaEditorBottomSheetFactory(
    private val client: UserClient,
    private val userDataRefreshTrigger: RefreshTrigger
) {
    operator fun invoke(media: MediaCardData): MediaEditorBottomSheet {
        return MediaEditorBottomSheet(client, media){
            userDataRefreshTrigger()
        }
    }
}

class MediaEditorBottomSheet(
    private val client: UserClient,
    private val media: MediaCardData,
    private val onUpdate: suspend () -> Unit,
): BottomSheetDialogFragment() {

    private var binding: MediaUpdateBottomSheetBinding by autoCleared()

    private suspend fun updateData() = withContext(Dispatchers.IO){
        val appMediaListStatus = UserMediaAnime(
            status = AppMediaListStatus.CURRENT,
            finishDate = null,
            startDate = null,
            score = binding.score.text.toString().toFloatOrNull(),
            watched = binding.progress.text.toString().toInt()
        )

        client.updateAnime(media.id, appMediaListStatus)
        onUpdate()
        dismiss()
    }

    private fun AppMediaListStatus.emptyOnUnknown() = when(this){
        AppMediaListStatus.UNKNOWN -> ""
        else -> name
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediaUpdateBottomSheetBinding.inflate(inflater, container, false)

        val episodeMaxValue = media.episodesAired ?: media.episodes
        if(episodeMaxValue != null) {
            binding.progress.filters = arrayOf(InputFilterMinMax(0, episodeMaxValue))
        }

        binding.increment.setOnClickListener {
            val initialValue = binding.progress.text.toString().toIntOrNull() ?: 0
            val finalString = initialValue.inc().coerceIn(0, episodeMaxValue).toString()
            binding.progress.setText(finalString)
        }

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            AppMediaListStatus.entries.filter { it != AppMediaListStatus.UNKNOWN }
        )

        binding.spinner.setAdapter(spinnerAdapter)
        binding.spinner.setText(media.userListStatus.emptyOnUnknown())

        binding.mediaDetails.text = "Update ${media.title}"
        binding.progress.setText((media.userWatched ?: "").toString())

        binding.saveBtn.setOnClickListener {
            lifecycleScope.launch { updateData() }
        }

        binding.deleteBtn.setOnClickListener {
            showToast("TODO: Not Implemented")
        }

        return binding.root
    }
}
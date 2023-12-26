package tel.jeelpa.otter.ui.fragments.mediaCommon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tel.jeelpa.otter.databinding.MediaUpdateBottomSheetBinding
import tel.jeelpa.otter.triggers.RefreshTrigger
import tel.jeelpa.otter.ui.generic.InputFilterMinMax
import tel.jeelpa.otter.ui.generic.MaterialSpinnerAdapter
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.plugininterface.tracker.models.AppMediaListStatus
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.UserMediaAnime
import tel.jeelpa.plugininterface.tracker.models.UserMediaManga
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
        when(media.type) {
            AppMediaType.ANIME -> client.updateAnime(media.id, UserMediaAnime(
                status = AppMediaListStatus.valueOf(binding.spinner.text.toString()),
                finishDate = null,
                startDate = null,
                score = binding.score.text.toString().toFloatOrNull(),
                watched = binding.progress.text.toString().toIntOrNull() ?: 0
            ))

            AppMediaType.MANGA -> client.updateManga(media.id, UserMediaManga(
                status = AppMediaListStatus.valueOf(binding.spinner.text.toString()),
                finishDate = null,
                startDate = null,
                score = binding.score.text.toString().toFloatOrNull(),
                chapters = binding.progress.text.toString().toIntOrNull() ?: 0,
            ))

            AppMediaType.UNKNOWN -> error("Cannot update media of unknown type")
        }
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

        val spinnerAdapter = MaterialSpinnerAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            AppMediaListStatus.entries.filter { it != AppMediaListStatus.UNKNOWN }
        )

        binding.spinner.apply {
            setAdapter(spinnerAdapter)

            setText(media.userListStatus.emptyOnUnknown())

            setOnItemClickListener { adapterView, _, idx, _ ->
                val selected = adapterView.getItemAtPosition(idx) as AppMediaListStatus
                if (selected == AppMediaListStatus.COMPLETED) {
                    // if COMPLETED is selected, autofill the maximum available episodes
                    binding.progress.setText((episodeMaxValue ?: "").toString())
                }
            }
        }

        binding.mediaDetails.text = "Update ${media.title}"
        binding.progress.setText((media.userWatched ?: "").toString())

        binding.saveBtn.setOnClickListener {
            // TODO: add checks for filling data before making api request
            lifecycleScope.launch {
                updateData()
                onUpdate()
                dismiss()
            }
        }

        binding.deleteBtn.setOnClickListener {
            showToast("Not Implemented")
            /**
             * This throws 400 for some reason, sometimes 401 too, WITH BEARER TOKEN // need to checkout why
             */
//            lifecycleScope.launch {
//                when(media.type) {
//                    AppMediaType.ANIME -> client.deleteAnime(media.id)
//                    AppMediaType.MANGA -> client.deleteManga(media.id)
//                    AppMediaType.UNKNOWN -> error("Cannot delete unknown media type")
//                }
//                onUpdate()
//                dismiss()
//            }
        }

        return binding.root
    }
}
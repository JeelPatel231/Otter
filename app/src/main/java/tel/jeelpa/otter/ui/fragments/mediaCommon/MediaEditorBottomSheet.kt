package tel.jeelpa.otter.ui.fragments.mediaCommon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tel.jeelpa.otter.databinding.SimpleTextViewBinding
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.repository.UserClient


class MediaEditorBottomSheetFactory(
    private val client: UserClient
) {
    operator fun invoke(id: Int, mediaType: AppMediaType): MediaEditorBottomSheet {
        return MediaEditorBottomSheet(client, id, mediaType)
    }
}

class MediaEditorBottomSheet(
    private val client: UserClient,
    private val id: Int,
    private val mediaType: AppMediaType,
): BottomSheetDialogFragment() {

    private var binding: SimpleTextViewBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SimpleTextViewBinding.inflate(inflater, container, false)

        binding.simpleTextView.text = "$id $mediaType"

        return binding.root
    }
}
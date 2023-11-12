package tel.jeelpa.otter.ui.fragments.character

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.ActivityCharacterBinding
import tel.jeelpa.otter.ui.adapters.MediaCardAdapter
import tel.jeelpa.otter.ui.generic.navigateToMediaDetails
import tel.jeelpa.otter.ui.generic.observeFlow
import javax.inject.Inject

@AndroidEntryPoint
class CharacterActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityCharacterBinding
    private val binding get() = _binding
    @Inject lateinit var markwon: Markwon
    private val characterDataViewModel: CharacterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val characterId = intent.getIntExtra("characterId", -1)
        if (characterId == -1) {
            throw NullPointerException("CharacterID was null")
        }
        _binding = ActivityCharacterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val rolesAdapter = MediaCardAdapter {
            navigateToMediaDetails(it)
        }
        binding.rolesRecycler.adapter = rolesAdapter
        binding.rolesRecycler.layoutManager = GridLayoutManager(this, 3)

        characterDataViewModel.character(characterId).observeFlow(this){
            // load better quality image when fetched
            binding.coverImage.load(it.avatar)
            binding.characterName.text = it.name
            binding.ageHolder.text = resources.getString(R.string.age, it.age)
            binding.birthdayHolder.text = resources.getString(R.string.birthday, it.dateOfBirth)
            binding.genderHolder.text = resources.getString(R.string.gender, it.gender)
            markwon.setMarkdown(binding.characterDescription,it.description ?: "")
            rolesAdapter.submitList(it.media)
        }
    }
}
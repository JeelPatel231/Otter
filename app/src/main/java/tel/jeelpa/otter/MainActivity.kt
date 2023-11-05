package tel.jeelpa.otter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.ActivityMainBinding
import tel.jeelpa.otter.ui.fragments.anime.AnimeFragment
import tel.jeelpa.otter.ui.fragments.home.HomeContainerFragment
import tel.jeelpa.otter.ui.fragments.manga.MangaFragment
import tel.jeelpa.otter.ui.generic.ViewPageNavigatorAdapter
import tel.jeelpa.otter.ui.generic.setupWithBottomNav


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding
        get() = _binding!!

    private val fragmentsList = arrayOf(
        AnimeFragment(),
        HomeContainerFragment(),
        MangaFragment()
    )

    private val viewPagerAdapter = ViewPageNavigatorAdapter(supportFragmentManager, lifecycle, fragmentsList)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fragmentPagerContainer.apply {
            adapter = viewPagerAdapter
            isUserInputEnabled = false
            setupWithBottomNav(binding.mainBottomNav, 1)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
package tel.jeelpa.otter.ui.generic

import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class ViewPageNavigatorAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragments: Array<Fragment>,
): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}

fun ViewPager2.setupWithBottomNav(bottomNavigationView: BottomNavigationView){
    val itemMap = bottomNavigationView.menu.children.mapIndexed { idx, item ->
        item.itemId to idx
    }.toMap()

    bottomNavigationView.setOnItemSelectedListener {
        currentItem = itemMap[it.itemId]!!
        true
    }
}

//fun BottomNavigationView.setupWithViewPager(viewPager : ViewPager2){
//    val itemMap = menu.children.mapIndexed { idx, item -> item.itemId to idx }.toMap()
//
//    setOnItemSelectedListener {
//        viewPager.currentItem = itemMap[it.itemId]!!
//        true
//    }
//}

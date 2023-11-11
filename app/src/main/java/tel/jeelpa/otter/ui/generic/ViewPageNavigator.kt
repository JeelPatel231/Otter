package tel.jeelpa.otter.ui.generic

import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.size
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
    // adapter must be set before setting up with bottom navigation view
    val adapterLocal = adapter
        ?: throw NullPointerException("Adapter MUST be set before setting up BottomNavigationView")
    // and the size MUST be same as the number of fragments and menu items
    assert(bottomNavigationView.menu.size == adapterLocal.itemCount)

    // use something else than a Map, something like reverse array lookup
    val itemMap = bottomNavigationView.menu.children.mapIndexed { idx, item ->
        item.itemId to idx
    }.toMap()


    // binding from navBar to ViewPager
    bottomNavigationView.setOnItemSelectedListener {
        setCurrentItem(itemMap[it.itemId]!!, false)
        true
    }

    // binding from viewPager to BottomNavBar
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            bottomNavigationView.menu[position].isChecked = true
        }
    })

    // has a delay, TODO: fix the delay
//    currentItem = default
}

//fun BottomNavigationView.setupWithViewPager(viewPager : ViewPager2){
//    val itemMap = menu.children.mapIndexed { idx, item -> item.itemId to idx }.toMap()
//
//    setOnItemSelectedListener {
//        viewPager.currentItem = itemMap[it.itemId]!!
//        true
//    }
//}

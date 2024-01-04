
import androidx.fragment.app.Fragment

import androidx.viewpager2.adapter.FragmentStateAdapter
import edu.vt.cs3714.tutorial_05.EditingFragment
import edu.vt.cs3714.tutorial_05.MainActivity
import edu.vt.cs3714.tutorial_05.PlayingFragment

class FragmentPagerAdapter(fa: MainActivity) : FragmentStateAdapter(fa!!) {
    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        return if (position == 0) {
            EditingFragment()
        } else {
            PlayingFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2 // Number of pages to swipe between
    }
}
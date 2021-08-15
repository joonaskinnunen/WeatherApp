package com.joonaskinnunen.weatherapp.ui.main

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.viewpager.widget.PagerAdapter
import com.joonaskinnunen.weatherapp.MainActivity

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        // A new placeholder fragment
        return PlaceholderFragment.newInstance(position)
    }

    override fun getCount(): Int {
        // Size of the forecast in MainActivity
        return MainActivity.forecasts.size
    }

}
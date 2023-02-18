package com.renju_note.isoo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.renju_note.isoo.databinding.ActivityMainBinding
import com.renju_note.isoo.dialog.ConfirmDialog
import com.renju_note.isoo.fragment.BoardFragment
import com.renju_note.isoo.fragment.SettingFragment
import com.renju_note.isoo.fragment.StorageFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainPager.isUserInputEnabled = true
        binding.mainPager.adapter = ViewPagerAdapter(supportFragmentManager,lifecycle)
        binding.mainPager.registerOnPageChangeCallback(PageChangeCallback())
        binding.bottomNavigationView.setOnItemSelectedListener { navigationSelected(it) }
        binding.mainPager.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    private fun navigationSelected(item: MenuItem): Boolean {
        val checked = item.setChecked(true)
        when (checked.itemId) {
            R.id.board_fragment_nav-> {
                binding.mainPager.currentItem = 0
                return true
            }
            R.id.storage_fragment_nav -> {
                binding.mainPager.currentItem = 1
                return true
            }
            R.id.setting_fragment_nav -> {
                binding.mainPager.currentItem = 2
                return true
            }
        }
        return false
    }

    private inner class ViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager,lifecycle){
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> BoardFragment()
                1 -> StorageFragment()
                2 -> SettingFragment()
                else -> error("no such position: $position")
            }
        }

    }

    private inner class PageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding.bottomNavigationView.selectedItemId = when (position) {
                0 -> R.id.board_fragment_nav
                1 -> R.id.storage_fragment_nav
                2 -> R.id.setting_fragment_nav
                else -> error("no such position: $position")
            }
        }
    }

    override fun onBackPressed() {
        val confirmDialog = ConfirmDialog(this, resources.getString(R.string.exit_confirm))
        confirmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        confirmDialog.setOnResponseListener(object : ConfirmDialog.OnResponseListener {
            override fun confirm() {
                confirmDialog.dismiss()
                finish()
            }
            override fun refuse() { confirmDialog.dismiss() }
        })
        confirmDialog.show()
    }

}
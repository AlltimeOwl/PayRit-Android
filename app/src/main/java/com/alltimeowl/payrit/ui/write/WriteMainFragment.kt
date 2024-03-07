package com.alltimeowl.payrit.ui.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentWriteMainBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class WriteMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentWriteMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentWriteMainBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            mainActivity.showBottomNavigationView()

            // 차용증 작성하기 클릭
            cardViewWriteIouWriteMain.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.IOU_MAIN_FRAGMENT, true, null)
            }

        }

    }

}
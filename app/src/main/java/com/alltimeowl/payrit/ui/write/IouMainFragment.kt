package com.alltimeowl.payrit.ui.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentIouMainBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class IouMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouMainBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            mainActivity.hideBottomNavigationView()

            materialToolbarIouMain.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_MAIN_FRAGMENT)
                }
            }

            // 빌려줄 예정
            cardViewLendIouMain.setOnClickListener {

                val bundle = Bundle()
                bundle.putString("writerRole", "CREDITOR")

                mainActivity.replaceFragment(MainActivity.IOU_TRANSACTIONAL_INFORMATION_FRAGMENT, true, bundle)
            }

            // 빌릴 예정
            cardViewBorrowIouMain.setOnClickListener {

                val bundle = Bundle()
                bundle.putString("writerRole", "DEBTOR")

                mainActivity.replaceFragment(MainActivity.IOU_TRANSACTIONAL_INFORMATION_FRAGMENT, true, bundle)
            }

        }
    }

}
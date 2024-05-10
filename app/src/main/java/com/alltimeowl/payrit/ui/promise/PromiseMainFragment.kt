package com.alltimeowl.payrit.ui.promise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alltimeowl.payrit.databinding.FragmentPromiseMainBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class PromiseMainFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPromiseMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentPromiseMainBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            mainActivity.hideBottomNavigationView()

            materialToolbarPromiseMain.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PROMISE_MAIN_FRAGMENT)
                }
            }

            // 약속 카드 만들기 클릭
            cardViewMakePromiseCardPromiseMain.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.PROMISE_CONTACT_FRAGMENT, true, null)
            }

        }
    }

}
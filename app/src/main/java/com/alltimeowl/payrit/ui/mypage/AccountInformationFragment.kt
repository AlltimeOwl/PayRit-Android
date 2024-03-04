package com.alltimeowl.payrit.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentAccountInformationBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class AccountInformationFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentAccountInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentAccountInformationBinding.inflate(layoutInflater)

        initUi()

        return binding.root
    }

    private fun initUi() {
        binding.run {
            mainActivity.hideBottomNavigationView()

            materialToolbarAccountInformation.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.ACCOUNT_INFORMATION_FRAGMENT)
                }
            }

            // 회원탈퇴
            textViewUserWithdrawalAccountInformation.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.WITHDRAWAL_FRAGMENT, true, null)
            }
        }
    }

}
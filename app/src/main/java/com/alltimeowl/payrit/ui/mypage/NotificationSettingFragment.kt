package com.alltimeowl.payrit.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentNotificationSettingBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class NotificationSettingFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentNotificationSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentNotificationSettingBinding.inflate(layoutInflater)

        initUi()

        return binding.root
    }

    private fun initUi() {
        binding.run {
            mainActivity.hideBottomNavigationView()

            materialToolbarNotificationSetting.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.NOTIFICATION_SETTING_FRAGMENT)
                }
            }

        }
    }

}
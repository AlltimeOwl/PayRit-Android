package com.alltimeowl.payrit.ui.mypage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alltimeowl.payrit.databinding.FragmentMyPageMainBinding
import com.alltimeowl.payrit.databinding.ItemUserLogoutBinding
import com.alltimeowl.payrit.ui.login.LoginActivity
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyPageMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentMyPageMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentMyPageMainBinding.inflate(layoutInflater)

        mainActivity.showBottomNavigationView()

        moveToAccountInformation()
        moveToNotificationSetting()
        moveToLogOut()

        return binding.root
    }

    // 계정 정보 클릭
    private fun moveToAccountInformation() {
        binding.linearLayoutAccountInformationMyPageMain.setOnClickListener {
            mainActivity.replaceFragment(MainActivity.ACCOUNT_INFORMATION_FRAGMENT, true, null)
        }

    }

    // 알림 설정 클릭
    private fun moveToNotificationSetting() {
        binding.linearLayoutNotificationSettingMyPageMain.setOnClickListener {
            mainActivity.replaceFragment(MainActivity.NOTIFICATION_SETTING_FRAGMENT, true, null)
        }
    }

    private fun moveToLogOut() {
        binding.linearLayoutLogoutMyPageMain.setOnClickListener {
            val itemUserLogoutBinding = ItemUserLogoutBinding.inflate(layoutInflater)
            val builder = MaterialAlertDialogBuilder(mainActivity)
            builder.setView(itemUserLogoutBinding.root)
            val dialog = builder.create()

            // 로그아웃 - 네
            itemUserLogoutBinding.textViewYesLogout.setOnClickListener {
                mainActivity.removeAllBackStack()

                val intent = Intent(mainActivity, LoginActivity::class.java)
                mainActivity.startActivity(intent)

                dialog.dismiss()
            }

            // 로그아웃 - 아니오
            itemUserLogoutBinding.textViewNoLogout.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

}
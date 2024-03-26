package com.alltimeowl.payrit.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentMyPageMainBinding
import com.alltimeowl.payrit.databinding.ItemUserLogoutBinding
import com.alltimeowl.payrit.ui.login.LoginActivity
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kakao.sdk.user.UserApiClient

class MyPageMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentMyPageMainBinding

    private lateinit var myPageViewModel: MyPageViewModel

    val TAG = "MyPageMainFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentMyPageMainBinding.inflate(layoutInflater)

        myPageViewModel = ViewModelProvider(this)[MyPageViewModel::class.java]

        mainActivity.showBottomNavigationView()

        initUi()
        moveToAccountInformation()
        moveToPaymentHistory()
        moveToNotificationSetting()
        moveToLogOut()

        return binding.root
    }

    private fun initUi() {
        binding.textViewUserNameMyPageMain.text = SharedPreferencesManager.getUserName()
    }

    // 계정 정보 클릭
    private fun moveToAccountInformation() {
        binding.linearLayoutAccountInformationMyPageMain.setOnClickListener {
            mainActivity.replaceFragment(MainActivity.ACCOUNT_INFORMATION_FRAGMENT, true, null)
        }

    }

    // 결재 내역 클릭
    private fun moveToPaymentHistory() {
        binding.linearLayoutPaymentHistoryMyPageMain.setOnClickListener {
            mainActivity.replaceFragment(MainActivity.PAYMENT_HISTORY_FRAGMENT, true, null)
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

                // 로그아웃
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.i(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                    }
                    else {
                        Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")

                        val accessToken = SharedPreferencesManager.getAccessToken()
                        myPageViewModel.logoutUser(accessToken)

                        SharedPreferencesManager.clearUserInfo()
                    }
                }

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
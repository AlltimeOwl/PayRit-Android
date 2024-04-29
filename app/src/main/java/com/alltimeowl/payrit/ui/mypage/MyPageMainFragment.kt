package com.alltimeowl.payrit.ui.mypage

import android.content.Intent
import android.net.Uri
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
import com.alltimeowl.payrit.ui.write.WriteMainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.user.UserApiClient

class MyPageMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentMyPageMainBinding

    private lateinit var myPageViewModel: MyPageViewModel
    private lateinit var writeMainViewModel: WriteMainViewModel

    private var accessToken = ""
    private var email = ""

    val TAG = "MyPageMainFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentMyPageMainBinding.inflate(layoutInflater)

        myPageViewModel = ViewModelProvider(this)[MyPageViewModel::class.java]
        writeMainViewModel = ViewModelProvider(this)[WriteMainViewModel::class.java]

        accessToken = SharedPreferencesManager.getAccessToken()
        email = SharedPreferencesManager.getUserEmail()

        mainActivity.showBottomNavigationView()

        initUi()
        moveToCertificationInfo()
        moveToAccountInformation()
        moveToPaymentHistory()
        moveToNotificationSetting()
        moveToKakaoInquiryTalk()
        moveToLogOut()
        moveToEmailWrite()

        return binding.root
    }

    private fun initUi() {
        binding.textViewUserNameMyPageMain.text = SharedPreferencesManager.getUserName()
        binding.textViewUserEmailMyPageMain.text = SharedPreferencesManager.getUserEmail()

        writeMainViewModel.checkCertification(
            accessToken,
            onSuccess = {
                binding.textViewVerificationCompleteMyPageMain.visibility = View.VISIBLE
                binding.imageViewVerificationCompleteMyPageMain.visibility = View.VISIBLE
            },
            onFailure = {
                binding.textViewVerificationCompleteMyPageMain.visibility = View.GONE
                binding.imageViewVerificationCompleteMyPageMain.visibility = View.GONE
            }
        )
    }

    // 본인인증 완료됨 클릭
    private fun moveToCertificationInfo() {
        binding.textViewVerificationCompleteMyPageMain.setOnClickListener {
            mainActivity.replaceFragment(MainActivity.CERTIFICATION_INFO_FRAGMENT, true, null)
        }
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

    // Payrit 클릭
    private fun moveToKakaoInquiryTalk() {
        binding.linearLayoutKakaoInquiryMyPageMain.setOnClickListener {
            // 카카오톡 채널 채팅 URL
            val url = TalkApiClient.instance.chatChannelUrl("_djxmxiG")

            // CustomTabs 로 열기
            KakaoCustomTabsClient.openWithDefault(requireContext(), url)
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

    // cs@payrit.info 클릭시
    private fun moveToEmailWrite() {
        binding.textViewPayritEmailMyPageMain.setOnClickListener {
            sendEmailWithGmail()
        }

    }

    private fun sendEmailWithGmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf("cs@payrit.info"))
        }

        // Check if Gmail app is available
        emailIntent.setPackage("com.google.android.gm")
        if (emailIntent.resolveActivity(mainActivity.packageManager) != null) {
            startActivity(emailIntent)
        } else {
            // If Gmail app is not available, start email chooser
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }
    }

}
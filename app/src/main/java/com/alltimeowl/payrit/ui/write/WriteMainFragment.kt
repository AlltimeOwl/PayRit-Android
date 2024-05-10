package com.alltimeowl.payrit.ui.write

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.BuildConfig
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.data.model.UserCertificationResponse
import com.alltimeowl.payrit.databinding.FragmentWriteMainBinding
import com.alltimeowl.payrit.databinding.ItemUserCertificationBinding
import com.alltimeowl.payrit.ui.home.HomeViewModel
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iamport.sdk.data.sdk.IamPortCertification
import com.iamport.sdk.domain.core.Iamport
import java.util.UUID

class WriteMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentWriteMainBinding

    private lateinit var viewModel: WriteMainViewModel
    private lateinit var homeViewModel: HomeViewModel

    val TAG = "WriteMainFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentWriteMainBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[WriteMainViewModel::class.java]
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        initUI()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Iamport.init(this@WriteMainFragment)
    }

    private fun initUI() {
        binding.run {

            mainActivity.showBottomNavigationView()

            // 차용증 작성하기 클릭
            cardViewWriteIouWriteMain.setOnClickListener {

                val accessToken = SharedPreferencesManager.getAccessToken()
                viewModel.checkCertification(
                    accessToken,
                    onSuccess = {
                        mainActivity.replaceFragment(MainActivity.IOU_MAIN_FRAGMENT, true, null)
                    },
                    onFailure = {
                        showAlertDialog()
                    }
                )

            }

            // 약속 카드 만들기 클릭
            cardViewWritePromiseWriteMain.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.PROMISE_MAIN_FRAGMENT, true, null)
            }

        }

    }

    private fun showAlertDialog() {
        val itemUserCertificationBinding = ItemUserCertificationBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemUserCertificationBinding.root)
        val dialog = builder.create()

        // 본인인증 - 아니오
        itemUserCertificationBinding.textViewNoCertification.setOnClickListener {
            dialog.dismiss()
        }

        // 본인인증 - 네
        itemUserCertificationBinding.textViewYesCertification.setOnClickListener {
            startIdentityVerification()
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun startIdentityVerification() {

        val userCode = BuildConfig.USER_CODE
        val certification = IamPortCertification(
            merchant_uid = getRandomMerchantUid(),
            company = "멋쟁이사자처럼"
        )

        Iamport.certification(userCode, iamPortCertification = certification) {
            Log.d(TAG, "it : ${it}")
            val userCertificationResponse = it?.imp_uid?.let { it1 -> UserCertificationResponse(it1) }

            val accessToken = SharedPreferencesManager.getAccessToken()
            if (it != null) {
                it.imp_uid?.let {
                    if (userCertificationResponse != null) {
                        viewModel.userCertification(accessToken, userCertificationResponse,
                            onSuccess = {
                                homeViewModel.reloadIou(accessToken)
                            }, onFailure = {
                                Log.d(TAG, "갱신 실패함")
                            }
                        )

                        mainActivity.replaceFragment(MainActivity.IOU_MAIN_FRAGMENT, true, null)
                    }
                }
            }

        }
    }

    private fun getRandomMerchantUid(): String {
        return "${UUID.randomUUID()}"
    }

}
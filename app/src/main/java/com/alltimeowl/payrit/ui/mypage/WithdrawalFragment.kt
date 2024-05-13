package com.alltimeowl.payrit.ui.mypage

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.data.model.SharedPreferencesPromiseManager
import com.alltimeowl.payrit.databinding.FragmentWithdrawalBinding
import com.alltimeowl.payrit.databinding.ItemUserLogoutBinding
import com.alltimeowl.payrit.databinding.ItemUserWithdrawalBinding
import com.alltimeowl.payrit.databinding.ItemUserWithdrawalCompleteBinding
import com.alltimeowl.payrit.ui.login.LoginActivity
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient

class WithdrawalFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentWithdrawalBinding
    private lateinit var spinnerAdapter: WithdrawalSpinnerAdapter

    private lateinit var myPageViewModel: MyPageViewModel

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val TAG = "WithdrawalFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentWithdrawalBinding.inflate(layoutInflater)
        firebaseAnalytics = Firebase.analytics

        myPageViewModel = ViewModelProvider(this)[MyPageViewModel::class.java]

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            materialToolbarWithdrawal.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.WITHDRAWAL_FRAGMENT)
                }
            }

            val category = resources.getStringArray(R.array.reason_withdrawal)

            spinnerWithdrawal.run {
                spinnerAdapter = WithdrawalSpinnerAdapter(mainActivity, R.layout.item_withdrawal_spinner, category)
                adapter = spinnerAdapter

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (position == 0) {
                            spinnerAdapter.isDropdownOpen = true
                        }
                        checkWithdrawal(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        spinnerAdapter.isDropdownOpen= false
                    }
                }

            }

        }
    }

    private fun checkWithdrawal(position: Int) {

        if (position == 0) {
            binding.buttonWithdrawal.setBackgroundResource(R.drawable.bg_gray_scale07_r12)

            // 버튼에 대한 클릭 리스너 제거
            binding.buttonWithdrawal.setOnClickListener(null)
        } else {
            binding.buttonWithdrawal.setBackgroundResource(R.drawable.bg_primary_mint_r12)

            // 탈퇴하기 버튼 클릭
            binding.buttonWithdrawal.setOnClickListener {
                showAlertDialog()
            }
        }

    }

    private fun showAlertDialog() {
        val itemUserWithdrawalBinding = ItemUserWithdrawalBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemUserWithdrawalBinding.root)
        val dialog = builder.create()

        // 회원탈퇴 - 네
        itemUserWithdrawalBinding.textViewYesWithdrawal.setOnClickListener {
            val bundle = Bundle()
            firebaseAnalytics.logEvent("withdrawal_PayRit_AOS", bundle)

            dialog.dismiss()

            // 연결 끊기
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Log.e(TAG, "연결 끊기 실패", error)
                }
                else {
                    Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")

                    val accessToken = SharedPreferencesManager.getAccessToken()
                    myPageViewModel.withdrawalUser(accessToken)

                    SharedPreferencesManager.clearUserInfo()
                    SharedPreferencesPromiseManager.clearPromises()
                }
            }

            showCompleteAlertDialog()
        }

        // 회원탈퇴 - 아니오
        itemUserWithdrawalBinding.textViewNoWithdrawal.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showCompleteAlertDialog() {
        val itemUserWithdrawalCompleteBinding = ItemUserWithdrawalCompleteBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemUserWithdrawalCompleteBinding.root)
        val dialog = builder.create()

        // 회원탈퇴 - 확인
        itemUserWithdrawalCompleteBinding.textViewWithdrawalComplete.setOnClickListener {
            dialog.dismiss()

            val intent = Intent(mainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        dialog.setCancelable(false)
        dialog.show()
    }


}
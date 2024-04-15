package com.alltimeowl.payrit.ui.payment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.SavePaymentInformationRequest
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentPaymentBinding
import com.alltimeowl.payrit.databinding.ItemCompletePaymentBinding
import com.alltimeowl.payrit.databinding.ItemPaymentCheckBinding
import com.alltimeowl.payrit.ui.home.HomeViewModel
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iamport.sdk.data.sdk.IamPortRequest
import com.iamport.sdk.domain.core.Iamport
import java.time.Instant


class PaymentFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPaymentBinding

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var paymentViewModel: PaymentViewModel

    private var accessToken: String = ""
    private var paperId: Int = 0
    private var buttonClickable = false

    val TAG = "PaymentFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentPaymentBinding.inflate(layoutInflater)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        paymentViewModel = ViewModelProvider(this)[PaymentViewModel::class.java]

        paperId = arguments?.getInt("paperId")!!

        accessToken = SharedPreferencesManager.getAccessToken()
        homeViewModel.getIouDetail(accessToken, paperId)
        paymentViewModel.getPaymentInformation(accessToken, paperId)

        initUI()
        observeData()
        checkBoxState()
        iouPayment()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Iamport.init(this@PaymentFragment)
    }

    private fun initUI() {
        binding.run {

            mainActivity.hideBottomNavigationView()

            materialToolbarPayment.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PAYMENT_FRAGMENT)
                }
            }

        }
    }

    private fun observeData() {
        homeViewModel.iouDetail.observe(viewLifecycleOwner) { iouDetailInfo ->
            Log.d(TAG, "iouDetailInfo : ${iouDetailInfo}")

            binding.progressBarPayment.visibility = View.GONE
            binding.scrollViewPayment.visibility = View.VISIBLE

            // 거래 내역
            binding.textViewTransactionAmountPayment.text = mainActivity.convertMoneyFormat(iouDetailInfo.paperFormInfo.primeAmount) + "원"
            binding.textViewTransactionDatePayment.text = mainActivity.convertDateFormat(iouDetailInfo.paperFormInfo.repaymentEndDate)

            // 추가 사항
            if ((iouDetailInfo.paperFormInfo.interestRate <= 0.0 || iouDetailInfo.paperFormInfo.interestRate > 20.00) && iouDetailInfo.paperFormInfo.specialConditions.isEmpty()) {
                binding.cardViewAdditionContractPayment.visibility = View.GONE
            } else {

                // 이자율
                if ((iouDetailInfo.paperFormInfo.interestRate <= 0.0 || iouDetailInfo.paperFormInfo.interestRate > 20.00)) {
                    binding.linearLayoutAdditionContractInterestRatePayment.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractInterestRatePayment.text = "${iouDetailInfo.paperFormInfo.interestRate}%"
                }

                // 이자 지급일
                if (iouDetailInfo.paperFormInfo.interestPaymentDate == 0) {
                    binding.linearLayoutAdditionContractDatePayment.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractDatePayment.text = "매월 ${iouDetailInfo.paperFormInfo.interestPaymentDate}일"
                }

                // 특약사항
                if (iouDetailInfo.paperFormInfo.specialConditions.isEmpty()) {
                    binding.linearLayoutAdditionContractPayment.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractPayment.text = iouDetailInfo.paperFormInfo.specialConditions
                }

            }

            // 빌려준 사람
            binding.textViewLendPersonNamePayment.text = iouDetailInfo.creditorProfile.name
            binding.textViewLendPersonPhoneNumberPayment.text = mainActivity.formatPhoneNumber(iouDetailInfo.creditorProfile.phoneNumber)
            if (iouDetailInfo.creditorProfile.address.isEmpty()) {
                binding.linearLayoutLendPersonAddressPayment.visibility = View.GONE
            } else {
                binding.textViewLendPersonAddressPayment.text = iouDetailInfo.creditorProfile.address
            }

            // 빌린 사람
            binding.textViewBorrowPersonNamePayment.text = iouDetailInfo.debtorProfile.name
            binding.textViewBorrowPersonPhoneNumberPayment.text = mainActivity.formatPhoneNumber(iouDetailInfo.debtorProfile.phoneNumber)
            if (iouDetailInfo.debtorProfile.address.isEmpty()) {
                binding.linearLayoutBorrowPersonAddressPayment.visibility = View.GONE
            } else {
                binding.textViewBorrowPersonAddressPayment.text = iouDetailInfo.debtorProfile.address
            }


        }

    }

    private fun checkBoxState() {

        binding.checkBoxPayment.setOnClickListener {

            buttonClickable = if (binding.checkBoxPayment.isChecked) {
                binding.buttonPayment.setBackgroundResource(R.drawable.bg_primary_mint_r12)
                true
            } else {
                binding.buttonPayment.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
                false
            }
        }

    }

    private fun iouPayment() {
        binding.buttonPayment.setOnClickListener {
            if (buttonClickable) {
                showAlertDialog()
            }
        }
    }

    private fun showAlertDialog() {
        val itemPaymentCheckBinding = ItemPaymentCheckBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemPaymentCheckBinding.root)
        val dialog = builder.create()

        // 결제 - 아니오
        itemPaymentCheckBinding.textViewNoPaymentCheck.setOnClickListener {
            dialog.dismiss()
        }

        // 결제 - 네
        itemPaymentCheckBinding.textViewYesPaymentCheck.setOnClickListener {
            getPaymentInfo()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getPaymentInfo() {
        paymentViewModel.getPaymentInformationResult.observe(viewLifecycleOwner) { result ->
            Log.d(TAG, "result : ${result}")

            val userCode = result?.PID
            val iamPortRequest = result?.let {
                IamPortRequest(
                    pg = it.PGCode,
                    merchant_uid = it.merchantUID,
                    name = it.name,
                    amount = it.amount.toString(),
                    buyer_email = it.buyerEmail,
                    buyer_name = it.buyerName,
                    buyer_tel = it.buyerTel
                )
            }

            if (userCode != null && iamPortRequest != null) {
                requestPayment(userCode, iamPortRequest)
            }

        }
    }

    private fun requestPayment(userCode : String, iamPortRequest : IamPortRequest) {
        Iamport.payment(userCode = userCode, iamPortRequest = iamPortRequest) { response ->
            Log.d(TAG, "response : ${response}")
            if (response != null) {
                if (response.imp_success == true) {
                    Log.d(TAG, "결제 성공: ${response.imp_uid}")

                    val savePaymentInformationRequest = SavePaymentInformationRequest(
                        paperId, transactionDate = Instant.now().toString(), amount = 1000, transactionType = "PAPER_TRANSACTION", response.imp_uid!!, response.merchant_uid!!, response.imp_success!!
                    )

                    savePaymentInfo(savePaymentInformationRequest)

                } else {
                    Log.d(TAG, "결제 실패: ${response.error_msg}")
                }
            }
        }
    }

    private fun savePaymentInfo(savePaymentInformationRequest : SavePaymentInformationRequest) {
        paymentViewModel.savePaymentInformation(accessToken, savePaymentInformationRequest)
        paymentViewModel.savePaymentInformationResult.observe(viewLifecycleOwner) { result ->
            if (result) {
                showCompleteAlertDialog()
            }
        }
    }

    private fun showCompleteAlertDialog() {

        mainActivity.removeFragment(MainActivity.PAYMENT_FRAGMENT)

        val itemCompletePaymentBinding = ItemCompletePaymentBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemCompletePaymentBinding.root)
        val dialog = builder.create()

        // 결제 완료 - 확인
        itemCompletePaymentBinding.textViewCheckCompletePayment.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
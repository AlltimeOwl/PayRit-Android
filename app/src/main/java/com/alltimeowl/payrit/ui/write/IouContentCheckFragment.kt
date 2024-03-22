package com.alltimeowl.payrit.ui.write

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.IouWriteRequest
import com.alltimeowl.payrit.data.model.IouWriteResponse
import com.alltimeowl.payrit.data.network.api.PayRitApi
import com.alltimeowl.payrit.databinding.FragmentIouContentCheckBinding
import com.alltimeowl.payrit.databinding.ItemCancelBinding
import com.alltimeowl.payrit.databinding.ItemMmsBinding
import com.alltimeowl.payrit.databinding.ItemMmsCompleteBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.alltimeowl.payrit.ui.main.MainActivity.Companion.accessToken
import com.alltimeowl.payrit.ui.main.MainActivity.Companion.loginUserName
import com.alltimeowl.payrit.ui.main.MainActivity.Companion.loginUserPhoneNumber
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IouContentCheckFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouContentCheckBinding

    private lateinit var writerRole: String
    private var amount: Int? = null
    private var calcedAmount: Int? = null
    private lateinit var transactionDate: String
    private lateinit var repaymentStartDate: String
    private lateinit var repaymentEndDate: String
    private lateinit var specialConditions: String
    private var interestRate: Float? = null
    private var interestPaymentDate: Int? = null

    private var creditorName = ""
    private var creditorPhoneNumber = ""
    private var creditorAddress = ""

    private var debtorName = ""
    private var debtorPhoneNumber = ""
    private var debtorAddress = ""

    private var opponentName = ""
    private var opponentPhoneNumber = ""
    private var opponentAddress = ""

    private lateinit var iouWriteRequest: IouWriteRequest

    val TAG = "IouContentCheckFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouContentCheckBinding.inflate(layoutInflater)

        writerRole = arguments?.getString("writerRole").toString()
        amount = arguments?.getInt("amount")
        calcedAmount = arguments?.getInt("calcedAmount")
        transactionDate = arguments?.getString("transactionDate").toString()
        repaymentStartDate = arguments?.getString("repaymentStartDate").toString()
        repaymentEndDate = arguments?.getString("repaymentEndDate").toString()
        specialConditions = arguments?.getString("specialConditions").toString()
        interestRate = arguments?.getFloat("interestRate")
        interestPaymentDate = arguments?.let {
            if (it.containsKey("interestPaymentDate")) it.getInt("interestPaymentDate")
            else null
        }

        when (writerRole) {
            "CREDITOR" -> {
                creditorName = arguments?.getString("creditorName").toString()
                creditorPhoneNumber = arguments?.getString("creditorPhoneNumber").toString()
                creditorAddress = arguments?.getString("creditorAddress").toString()

                opponentName = arguments?.getString("debtorName").toString()
                opponentPhoneNumber = arguments?.getString("debtorPhoneNumber").toString()
                opponentAddress = arguments?.getString("debtorAddress").toString()
            }

            "DEBTOR" -> {
                opponentName = arguments?.getString("creditorName").toString()
                opponentPhoneNumber = arguments?.getString("creditorPhoneNumber").toString()
                opponentAddress = arguments?.getString("creditorAddress").toString()

                debtorName = arguments?.getString("debtorName").toString()
                debtorPhoneNumber = arguments?.getString("debtorPhoneNumber").toString()
                debtorAddress = arguments?.getString("debtorAddress").toString()
            }
        }

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            materialToolbarIouContentCheck.run {
                // 뒤로가기 버튼
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_CONTENT_CHECK_FRAGMENT)
                }

                // X 버튼
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.item_cancel -> showAlertDialog()
                    }
                    false
                }

                settingIou()

                // 요청 전송하기
                buttonSendIouContentCheck.setOnClickListener {
                    showMmsAlertDialog()
                }

            }
        }
    }

    // 페이릿 내용 설정
    private fun settingIou() {
        binding.run {

            // 거래 내역
            textViewTransactionAmountIouContentCheck.text = amount.toString()
            textViewTransactionDateIouContentCheck.text = repaymentStartDate

            // 특약 사항

            val interestAmount = (calcedAmount ?: 0) - (amount ?: 0)

            if (interestAmount == 0 && interestPaymentDate == null && specialConditions.isEmpty()) {
                textViewSpecialContractTitleIouContentCheck.visibility = View.GONE
                cardViewSpecialContractIouContentCheck.visibility = View.GONE
            } else {

                if (interestAmount == 0) {
                    linearLayoutSpecialContractInterestAmountIouContentCheck.visibility = View.GONE
                } else {
                    textViewSpecialContractInterestAmountIouContentCheck.text = interestAmount.toString()
                }

                if (interestPaymentDate == null) {
                    linearLayoutSpecialContractDateIouContentCheck.visibility = View.GONE
                } else {
                    textViewSpecialContractDateIouContentCheck.text = "매월 ${interestPaymentDate}일"
                }

                if (specialConditions.isEmpty()) {
                    linearLayoutSpecialContractIouContentCheck.visibility = View.GONE
                } else {
                    textViewSpecialContractIouContentCheck.text = specialConditions
                }
            }

            when (writerRole) {
                "CREDITOR" -> {

                    // 로그인한 유저가 빌려준 사람일 때
                    textViewLendPersonNameIouContentCheck.text = loginUserName
                    textViewLendPersonPhoneNumberIouContentCheck.text = mainActivity.convertPhoneNumber(loginUserPhoneNumber)
                    if (creditorAddress.isNotEmpty()) {
                        textViewLendPersonAddressIouContentCheck.text = creditorAddress
                    } else {
                        linearLayoutLendPersonAddressIouContentCheck.visibility = View.GONE
                    }

                    textViewBorrowPersonNameIouContentCheck.text = opponentName
                    textViewBorrowPersonPhoneNumberIouContentCheck.text = opponentPhoneNumber
                    if (opponentAddress.isNotEmpty()) {
                        textViewBorrowPersonAddressIouContentCheck.text = opponentAddress
                    } else {
                        linearLayoutBorrowPersonAddressIouContentCheck.visibility = View.GONE
                    }

                }

                "DEBTOR" -> {

                    // 로그인한 유저가 값는 사람일 때
                    textViewLendPersonNameIouContentCheck.text = opponentName
                    textViewLendPersonPhoneNumberIouContentCheck.text = opponentPhoneNumber
                    if (opponentAddress.isNotEmpty()) {
                        textViewLendPersonAddressIouContentCheck.text = opponentAddress
                    } else {
                        linearLayoutLendPersonAddressIouContentCheck.visibility = View.GONE
                    }

                    textViewBorrowPersonNameIouContentCheck.text = loginUserName
                    textViewBorrowPersonPhoneNumberIouContentCheck.text = mainActivity.convertPhoneNumber(loginUserPhoneNumber)
                    if (debtorAddress.isNotEmpty()) {
                        textViewBorrowPersonAddressIouContentCheck.text = debtorAddress
                    } else {
                        linearLayoutBorrowPersonAddressIouContentCheck.visibility = View.GONE
                    }

                }
            }

        }
    }

    private fun showAlertDialog() {
        val itemCancelBinding = ItemCancelBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemCancelBinding.root)
        val dialog = builder.create()

        // 작성 중단 - 네
        itemCancelBinding.textViewYesCancel.setOnClickListener {
            dialog.dismiss()

            mainActivity.removeAllBackStack()
        }

        // 작성 중단 - 아니오
        itemCancelBinding.textViewNoCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showMmsAlertDialog() {
        val itemMmsBinding = ItemMmsBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemMmsBinding.root)
        val dialog = builder.create()

        // MMS 요청 전송 - 아니오
        itemMmsBinding.textViewNoMms.setOnClickListener {
            dialog.dismiss()
        }

        // MMS 요청 전송 - 네
        itemMmsBinding.textViewYesMms.setOnClickListener {
            dialog.dismiss()
            writeIouApi()
            showMmsCompleteAlertDialog()
        }

        dialog.show()
    }

    private fun showMmsCompleteAlertDialog() {
        val itemMmsCompleteBinding = ItemMmsCompleteBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemMmsCompleteBinding.root)
        val dialog = builder.create()

        // MMS 요청완료 - 확인
        itemMmsCompleteBinding.textViewCompleteMms.setOnClickListener {
            dialog.dismiss()

            mainActivity.removeAllBackStack()
            mainActivity.selectBottomNavigationItem(R.id.home_menu)
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun writeIouApi() {

        when(writerRole) {
            "CREDITOR" -> {
                iouWriteRequest = IouWriteRequest(writerRole, amount, calcedAmount, transactionDate, repaymentStartDate, repaymentEndDate, specialConditions, interestRate, interestPaymentDate,
                    creditorName, creditorPhoneNumber, creditorAddress,
                    debtorName = opponentName, debtorPhoneNumber = mainActivity.convertToInternationalFormat(opponentPhoneNumber), debtorAddress = opponentAddress)
            }

            "DEBTOR" -> {
                iouWriteRequest = IouWriteRequest(writerRole, amount, calcedAmount, transactionDate, repaymentStartDate, repaymentEndDate, specialConditions, interestRate, interestPaymentDate,
                    creditorName = opponentName, creditorPhoneNumber = mainActivity.convertToInternationalFormat(opponentPhoneNumber), creditorAddress = opponentAddress,
                    debtorName, debtorPhoneNumber, debtorAddress)
            }
        }

        val payRitApi: PayRitApi

        val retrofit = Retrofit.Builder()
            .baseUrl("https://payrit.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        payRitApi = retrofit.create(PayRitApi::class.java)

        Log.d(TAG, "iouWriteRequest : ${iouWriteRequest}")

        val call = payRitApi.iouWrite("Bearer ${accessToken}", iouWriteRequest)
        call.enqueue(object : Callback<IouWriteResponse> {
            override fun onResponse(
                call: Call<IouWriteResponse>,
                response: Response<IouWriteResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "성공: ${response.code()}")
                    Log.d(TAG, "response.headers : ${response.headers()}")
                } else {
                    Log.d(TAG, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<IouWriteResponse>, t: Throwable) {
                Log.d(TAG, "네트워크 오류: ${t.message}")
            }

        })
    }

}
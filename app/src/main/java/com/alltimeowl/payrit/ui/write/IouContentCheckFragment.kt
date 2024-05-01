package com.alltimeowl.payrit.ui.write

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.IouWriteRequest
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentIouContentCheckBinding
import com.alltimeowl.payrit.databinding.ItemCancelBinding
import com.alltimeowl.payrit.databinding.ItemKakaolBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link

class IouContentCheckFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouContentCheckBinding

    private lateinit var writerRole: String
    private var amount: Int? = null
    private var interest: Int? = null
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

    private var writeUserName = ""

    private lateinit var iouWriteRequest: IouWriteRequest

    private lateinit var viewModel: IouWriteViewModel

    val TAG = "IouContentCheckFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouContentCheckBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[IouWriteViewModel::class.java]

        writerRole = arguments?.getString("writerRole").toString()
        amount = arguments?.getInt("amount")
        interest = arguments?.getInt("interest")
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

                writeUserName = creditorName
            }

            "DEBTOR" -> {
                opponentName = arguments?.getString("creditorName").toString()
                opponentPhoneNumber = arguments?.getString("creditorPhoneNumber").toString()
                opponentAddress = arguments?.getString("creditorAddress").toString()

                debtorName = arguments?.getString("debtorName").toString()
                debtorPhoneNumber = arguments?.getString("debtorPhoneNumber").toString()
                debtorAddress = arguments?.getString("debtorAddress").toString()

                writeUserName = debtorName
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

                // 공유하기
                buttonSendIouContentCheck.setOnClickListener {
                    showKakaoDialog()
                }

            }
        }
    }

    // 페이릿 내용 설정
    private fun settingIou() {
        binding.run {

            // 거래 내역
            textViewTransactionAmountIouContentCheck.text = mainActivity.convertMoneyFormat(amount!!) + "원"
            textViewTransactionDateIouContentCheck.text = repaymentStartDate

            // 특약 사항
            if ((interestRate!! <= 0.0 || interestRate!! > 20.00) && interestPaymentDate == null && specialConditions.isEmpty()) {
                cardViewSpecialContractIouContentCheck.visibility = View.GONE
            } else {

                if ((interestRate!! <= 0.0 || interestRate!! > 20.00)) {
                    linearLayoutSpecialContractInterestAmountRateIouContentCheck.visibility = View.GONE
                } else {
                    textViewSpecialContractInterestAmountRateIouContentCheck.text = "${interestRate}%"
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
                    textViewLendPersonNameIouContentCheck.text = SharedPreferencesManager.getUserName()
                    textViewLendPersonPhoneNumberIouContentCheck.text = mainActivity.convertPhoneNumber(SharedPreferencesManager.getUserPhoneNumber())
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

                    textViewBorrowPersonNameIouContentCheck.text = SharedPreferencesManager.getUserName()
                    textViewBorrowPersonPhoneNumberIouContentCheck.text = mainActivity.convertPhoneNumber(SharedPreferencesManager.getUserPhoneNumber())
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

    private fun showKakaoDialog() {
        val itemKakaolBinding = ItemKakaolBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemKakaolBinding.root)
        val dialog = builder.create()

        // 카카오톡 공유 - 아니오
        itemKakaolBinding.textViewNoKakao.setOnClickListener {
            dialog.dismiss()
        }

        // 카카오톡 공유 - 네
        itemKakaolBinding.textViewYesKakao.setOnClickListener {
            dialog.dismiss()
            writeIouApi()
            sendKakao()
        }

        dialog.show()
    }

    private fun writeIouApi() {

        when(writerRole) {
            "CREDITOR" -> {
                iouWriteRequest = IouWriteRequest(writerRole, amount, interest, transactionDate, repaymentStartDate, repaymentEndDate, specialConditions, interestRate, interestPaymentDate,
                    creditorName, creditorPhoneNumber, creditorAddress,
                    debtorName = opponentName, debtorPhoneNumber = mainActivity.convertToInternationalFormat(opponentPhoneNumber), debtorAddress = opponentAddress)
            }

            "DEBTOR" -> {
                iouWriteRequest = IouWriteRequest(writerRole, amount, interest, transactionDate, repaymentStartDate, repaymentEndDate, specialConditions, interestRate, interestPaymentDate,
                    creditorName = opponentName, creditorPhoneNumber = mainActivity.convertToInternationalFormat(opponentPhoneNumber), creditorAddress = opponentAddress,
                    debtorName, debtorPhoneNumber, debtorAddress)
            }
        }

        val accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.iouWrite(accessToken, iouWriteRequest)
    }

    private fun sendKakao() {
        val defaultFeed = FeedTemplate(
            content = Content(
                title = "${writeUserName}님이 작성하신\n" +
                        "페이릿 차용증이 작성되었습니다.\n" +
                        "앱에서 확인해주세요",
                imageUrl = "https://github.com/wjdwntjd55/Blog/assets/73345198/1c29ffef-0176-4add-9e1b-cfb4d8e321cc",
                link = Link(
                    webUrl = "https://developers.kakao.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            ),
            buttons = listOf(
                Button(
                    "앱으로 보기",
                    Link(
                        androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                        iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                    )
                )
            )
        )

        // 피드 메시지 보내기

        // 카카오톡 설치여부 확인
        if (ShareClient.instance.isKakaoTalkSharingAvailable(requireActivity())) {
            // 카카오톡으로 카카오톡 공유 가능
            ShareClient.instance.shareDefault(
                requireActivity(),
                defaultFeed
            ) { sharingResult, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡 공유 실패", error)
                } else if (sharingResult != null) {
                    Log.d(TAG, "카카오톡 공유 성공 ${sharingResult.intent}")
                    startActivity(sharingResult.intent)

                    mainActivity.selectBottomNavigationItem(R.id.home_menu)

                    // 카카오톡 공유에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                    Log.w(TAG, "Warning Msg: ${sharingResult.warningMsg}")
                    Log.w(TAG, "Argument Msg: ${sharingResult.argumentMsg}")
                }
            }

        } else {
            // 카카오톡 미설치: 웹 공유 사용 권장
            // 웹 공유 예시 코드
            val sharerUrl = WebSharerClient.instance.makeDefaultUrl(defaultFeed)

            // CustomTabs으로 웹 브라우저 열기

            // 1. CustomTabsServiceConnection 지원 브라우저 열기
            // ex) Chrome, 삼성 인터넷, FireFox, 웨일 등
            try {
                KakaoCustomTabsClient.openWithDefault(requireContext(), sharerUrl)
                mainActivity.activityMainBinding.bottomNavigationViewMain.selectedItemId = R.id.home_menu
            } catch (e: UnsupportedOperationException) {
                // CustomTabsServiceConnection 지원 브라우저가 없을 때의 예외 처리
            }

        }
    }
}
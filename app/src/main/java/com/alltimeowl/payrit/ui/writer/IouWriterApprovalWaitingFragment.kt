package com.alltimeowl.payrit.ui.writer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentIouWriterApprovalWaitingBinding
import com.alltimeowl.payrit.ui.home.HomeViewModel
import com.alltimeowl.payrit.ui.main.MainActivity
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link

class IouWriterApprovalWaitingFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouWriterApprovalWaitingBinding

    private lateinit var viewModel: HomeViewModel

    private var paperId: Int = 0
    private var writeUserName = ""

    val TAG = "IouWriterApprovalWaitingFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouWriterApprovalWaitingBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        paperId = arguments?.getInt("paperId")!!

        val accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.getIouDetail(accessToken, paperId)

        initUI()
        observeData()
        KakaoTalkShare()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            mainActivity.hideBottomNavigationView()

            materialToolbarIouWriterApprovalWaiting.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_WRITER_APPROVAL_WAITING_FRAGMENT)
                }
            }

        }
    }

    private fun observeData() {
        viewModel.iouDetail.observe(viewLifecycleOwner) { iouDetailInfo ->
            Log.d(TAG, "iouDetailInfo : ${iouDetailInfo}")

            when(iouDetailInfo.memberRole) {
                "CREDITOR" -> writeUserName = iouDetailInfo.creditorProfile.name
                "DEBTOR" -> writeUserName = iouDetailInfo.debtorProfile.name
            }

            binding.progressBarIouWriterApprovalWaiting.visibility = View.GONE
            binding.scrollViewIouWriterApprovalWaiting.visibility = View.VISIBLE

            // 거래 내역
            binding.textViewTransactionAmountIouWriterApprovalWaiting.text = mainActivity.convertMoneyFormat(iouDetailInfo.paperFormInfo.primeAmount) + "원"
            binding.textViewTransactionDateIouWriterApprovalWaiting.text = mainActivity.convertDateFormat(iouDetailInfo.paperFormInfo.repaymentEndDate)

            // 추가 사항
            if ((iouDetailInfo.paperFormInfo.interestRate <= 0.0 || iouDetailInfo.paperFormInfo.interestRate > 20.00) && iouDetailInfo.paperFormInfo.specialConditions.isEmpty()) {
                binding.cardViewAdditionContractIouWriterApprovalWaiting.visibility = View.GONE
            } else {

                // 이자율
                if ((iouDetailInfo.paperFormInfo.interestRate <= 0.0 || iouDetailInfo.paperFormInfo.interestRate > 20.00)) {
                    binding.linearLayoutAdditionContractInterestRateIouWriterApprovalWaiting.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractInterestRateIouWriterApprovalWaiting.text = "${iouDetailInfo.paperFormInfo.interestRate}%"
                }

                // 이자 지급일
                if (iouDetailInfo.paperFormInfo.interestPaymentDate == 0) {
                    binding.linearLayoutAdditionContractDateIouWriterApprovalWaiting.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractDateIouWriterApprovalWaiting.text = "매월 ${iouDetailInfo.paperFormInfo.interestPaymentDate}일"
                }

                // 특약사항
                if (iouDetailInfo.paperFormInfo.specialConditions.isEmpty()) {
                    binding.linearLayoutAdditionContractIouWriterApprovalWaiting.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractIouWriterApprovalWaiting.text = iouDetailInfo.paperFormInfo.specialConditions
                }

            }

            // 빌려준 사람
            binding.textViewLendPersonNameIouWriterApprovalWaiting.text = iouDetailInfo.creditorProfile.name
            binding.textViewLendPersonPhoneNumberIouWriterApprovalWaiting.text = mainActivity.formatPhoneNumber(iouDetailInfo.creditorProfile.phoneNumber)
            if (iouDetailInfo.creditorProfile.address.isEmpty()) {
                binding.linearLayoutLendPersonAddressIouWriterApprovalWaiting.visibility = View.GONE
            } else {
                binding.textViewLendPersonAddressIouWriterApprovalWaiting.text = iouDetailInfo.creditorProfile.address
            }

            // 빌린 사람
            binding.textViewBorrowPersonNameIouWriterApprovalWaiting.text = iouDetailInfo.debtorProfile.name
            binding.textViewBorrowPersonPhoneNumberIouWriterApprovalWaiting.text = mainActivity.formatPhoneNumber(iouDetailInfo.debtorProfile.phoneNumber)
            if (iouDetailInfo.debtorProfile.address.isEmpty()) {
                binding.linearLayoutBorrowPersonAddressIouWriterApprovalWaiting.visibility = View.GONE
            } else {
                binding.textViewBorrowPersonAddressIouWriterApprovalWaiting.text = iouDetailInfo.debtorProfile.address
            }

        }
    }

    private fun KakaoTalkShare() {
        binding.buttonIouWriterApprovalWaiting.setOnClickListener {
            val defaultFeed = FeedTemplate(
                content = Content(
                    title = "${writeUserName}님이 작성하신\n" +
                            "페이릿 차용증이 작성되었습니다.\n" +
                            "앱에서 확인해주세요",
                    imageUrl = "https://github.com/AlltimeOwl/PayRit-Android/assets/73345198/4c9779ae-eba6-4f63-a81c-e7b3517d3e6d",
                    imageWidth = 400,
                    imageHeight = 200,
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
                } catch (e: UnsupportedOperationException) {
                    // CustomTabsServiceConnection 지원 브라우저가 없을 때의 예외 처리
                }

            }
        }
    }

}
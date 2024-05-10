package com.alltimeowl.payrit.ui.promise

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.PromiseData
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.data.model.WritePromiseRequest
import com.alltimeowl.payrit.databinding.FragmentPromiseMakeBinding
import com.alltimeowl.payrit.databinding.ItemFailurePromiseBinding
import com.alltimeowl.payrit.databinding.ItemKakaoPromiseBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import java.text.SimpleDateFormat
import java.util.Locale

class PromiseMakeFragment : Fragment(), PromiseMakeAdapter.OnImageTypeSelectedListener {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPromiseMakeBinding

    private lateinit var viewModel: PromiseViewModel

    private var amount = ""
    private var promiseStartDate = ""
    private var promiseEndDate = ""
    private var contents = ""
    private var promiseDataList: ArrayList<PromiseData>? = null

    private var allRecipientNames = ""
    private var allRecipientPhoneNumbers = ""
    private var promiseImageType = "PRESENT"

    val TAG = "PromiseMakeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentPromiseMakeBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[PromiseViewModel::class.java]

        amount = arguments?.getString("amount").toString()
        promiseStartDate = arguments?.getString("promiseStartDate").toString()
        promiseEndDate = arguments?.getString("promiseEndDate").toString()
        contents = arguments?.getString("contents").toString()

        promiseDataList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("promiseDataList", PromiseData::class.java)
        }else {
            arguments?.getParcelableArrayList("promiseDataList")
        }

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            materialToolbarPromiseMake.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PROMISE_MAKE_FRAGMENT)
                }

                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.item_cancel -> mainActivity.showCancelAlertDialog()
                    }
                    false
                }
            }

            recyclerViewPromiseMake.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = PromiseMakeAdapter(binding, this@PromiseMakeFragment)
            }

            // 화면에 정보 표시
            textViewAmountPromiseMake.text = amount
            textViewPeriodPromiseMake.text = promiseStartDate + "~" + promiseEndDate
            textViewSenderNamePromiseMake.text = SharedPreferencesManager.getUserName()
            textViewContentsPromiseMake.text = contents

            Log.d(TAG, "promiseDataList : $promiseDataList")

            allRecipientNames = promiseDataList?.joinToString(separator = ",", transform = { it.name?.replace(" ", "") ?: "" }).toString()
            allRecipientPhoneNumbers = promiseDataList?.joinToString(separator = ",", transform = { it.phoneNumber?.replace(" ", "") ?: "" }).toString()

            recyclerViewRecipientPromiseMake.run {
                layoutManager = LinearLayoutManager(context)
                adapter = promiseDataList?.let { RecipientAdapter(it) }
            }

            // 저장하기 버튼 클릭시
            buttonSavePromiseMake.setOnClickListener {
                val accessToken = SharedPreferencesManager.getAccessToken()

                val writePromiseRequest = WritePromiseRequest(
                    amount = amount.toInt(), promiseStartDate = convertDate(promiseStartDate), promiseEndDate = convertDate(promiseEndDate), contents = contents, writerName = SharedPreferencesManager.getUserName(),
                    participantsName = allRecipientNames, participantsPhone = allRecipientPhoneNumbers, promiseImageType = promiseImageType
                )

                Log.d(TAG, "writePromiseRequest : $writePromiseRequest")

                viewModel.writePromise(accessToken, writePromiseRequest,
                    onSuccess = { promiseId ->
                        kaKaoAlertDialog(promiseId)
                    }, onFailure = {
                        showFailureAlertDialog()
                    }
                )
            }

        }
    }
    private fun kaKaoAlertDialog(promiseId: Long) {
        val itemKakaoPromiseBinding = ItemKakaoPromiseBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemKakaoPromiseBinding.root)
        val dialog = builder.create()

        // 아니오
        itemKakaoPromiseBinding.textViewNoKakaoPromise.setOnClickListener {
            dialog.dismiss()
            mainActivity.removeAllBackStack()
        }

        // 네
        itemKakaoPromiseBinding.textViewYesKakaoPromise.setOnClickListener {
            dialog.dismiss()
            sendKakao(promiseId)
        }

        dialog.show()
    }

    // 저장 하기 실패
    private fun showFailureAlertDialog() {
        val itemFailurePromiseBinding = ItemFailurePromiseBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemFailurePromiseBinding.root)
        val dialog = builder.create()

        itemFailurePromiseBinding.textViewCheckFailurePromise.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onImageTypeSelected(imageType: String) {
        promiseImageType = imageType
    }

    private fun convertDate(originalDate: String): String {
        // 원본 날짜 포맷을 정의합니다.
        val originalFormat = SimpleDateFormat("yyyy.M.d", Locale.getDefault())
        // 변경하고자 하는 날짜 포맷을 정의합니다.
        val targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 원본 날짜 문자열을 Date 객체로 파싱합니다.
        val date = originalFormat.parse(originalDate)
        // 파싱된 Date 객체를 새로운 포맷으로 변환합니다.
        return if (date != null) targetFormat.format(date) else ""
    }

    private fun sendKakao(promiseId: Long) {

        val defaultFeed : FeedTemplate

        defaultFeed = FeedTemplate(
            content = Content(
                title = "${SharedPreferencesManager.getUserName()}님이 작성하신\n" +
                        "페이릿 약속이 작성되었습니다.\n" +
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
                        androidExecutionParams = mapOf("promiseId" to "${promiseId.toInt()}"),
                        iosExecutionParams = mapOf("promiseId" to "${promiseId.toInt()}"),
                    )
                )
            )
        )

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

                    mainActivity.removeAllBackStack()

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
                mainActivity.removeAllBackStack()
            } catch (e: UnsupportedOperationException) {
                // CustomTabsServiceConnection 지원 브라우저가 없을 때의 예외 처리
            }

        }

    }

}
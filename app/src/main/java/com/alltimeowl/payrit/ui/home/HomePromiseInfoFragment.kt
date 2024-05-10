package com.alltimeowl.payrit.ui.home

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
import com.alltimeowl.payrit.data.model.PromiseDetail
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentHomePromiseInfoBinding
import com.alltimeowl.payrit.databinding.ItemDeleteBinding
import com.alltimeowl.payrit.databinding.ItemFailureDeleteBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.alltimeowl.payrit.ui.promise.PromiseViewModel
import com.alltimeowl.payrit.ui.promise.RecipientAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import java.text.NumberFormat
import java.util.Locale

class HomePromiseInfoFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentHomePromiseInfoBinding

    lateinit var viewModel: PromiseViewModel

    private var accessToken = ""
    private var promiseDetail: PromiseDetail? = null

    val TAG = "HomePromiseInfoFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentHomePromiseInfoBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[PromiseViewModel::class.java]

        accessToken = SharedPreferencesManager.getAccessToken()

        promiseDetail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("promiseDetailInfo", PromiseDetail::class.java)
        }else {
            arguments?.getParcelable("promiseDetailInfo")
        }

        Log.d(TAG, "promiseDetail : ${promiseDetail}")

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            mainActivity.hideBottomNavigationView()

            materialToolbarHomePromiseInfo.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.HOME_PROMISE_INFO_FRAGMENT)
                }

                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.item_delete -> showDeleteAlertDialog()
                    }
                    false
                }
            }

            // 약속 정보 (금액, 기간)
            val formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(promiseDetail?.amount)
            textViewAmountHomePromiseInfo.text = formattedAmount + "원"
            textViewPeriodHomePromiseInfo.text = "${promiseDetail?.promiseStartDate?.let { mainActivity.convertDateFormat(it) }}~${promiseDetail?.promiseEndDate?.let { mainActivity.convertDateFormat(it) }}"

            // 보낸 사람
            textViewSenderNameHomePromiseInfo.text = promiseDetail?.writerName

            // 받는 사람
            val participantsNames = promiseDetail?.participants?.map { it.participantsName }
            val participantsPhones = promiseDetail?.participants?.map { it.participantsPhone }

            val allParticipantsInfo = ArrayList<PromiseData>()

            // participantsNames와 participantsPhones 리스트의 각 항목을 짝지어 PromiseData 객체로 변환하여 allParticipantsInfo에 추가
            participantsNames?.zip(participantsPhones ?: emptyList())?.forEach { (name, phone) ->
                allParticipantsInfo.add(PromiseData(name, phone))
            }

            Log.d(TAG, "allParticipantsInfo : $allParticipantsInfo")

            recyclerViewRecipientHomePromiseInfo.run {
                layoutManager = LinearLayoutManager(context)
                adapter = RecipientAdapter(allParticipantsInfo)
            }

            // 약속 내용
            textViewContentsHomePromiseInfo.text = promiseDetail?.contents

            // 공유 하기 버튼
            buttonShareHomePromiseInfo.setOnClickListener {
                promiseDetail?.let { it1 -> sendKakao(it1.promiseId) }
            }

        }

    }

    private fun showDeleteAlertDialog() {
        val itemDeleteBinding = ItemDeleteBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemDeleteBinding.root)
        val dialog = builder.create()

        itemDeleteBinding.textViewYesDelete.setOnClickListener {
            promiseDetail?.let { it1 ->
                viewModel.deletePromise(accessToken, it1.promiseId,
                    onSuccess = {
                        mainActivity.removeAllBackStack()
                    }, onFailure = {
                        showFailureAlertDialog()
                    }
                )
            }
            dialog.dismiss()
        }

        itemDeleteBinding.textViewNoDelete.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showFailureAlertDialog() {
        val itemFailureDeleteBinding = ItemFailureDeleteBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemFailureDeleteBinding.root)
        val dialog = builder.create()

        itemFailureDeleteBinding.textViewCheckFailureDelete.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun sendKakao(promiseId: Int) {

        val defaultFeed = FeedTemplate(
            content = Content(
                title = "${promiseDetail?.writerName}님이 작성하신\n" +
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
                        androidExecutionParams = mapOf("promiseId" to "$promiseId"),
                        iosExecutionParams = mapOf("promiseId" to "$promiseId"),
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
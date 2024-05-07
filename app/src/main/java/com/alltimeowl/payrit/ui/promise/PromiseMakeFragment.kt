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

    fun convertDate(originalDate: String): String {
        // 원본 날짜 포맷을 정의합니다.
        val originalFormat = SimpleDateFormat("yyyy.M.d", Locale.getDefault())
        // 변경하고자 하는 날짜 포맷을 정의합니다.
        val targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 원본 날짜 문자열을 Date 객체로 파싱합니다.
        val date = originalFormat.parse(originalDate)
        // 파싱된 Date 객체를 새로운 포맷으로 변환합니다.
        return if (date != null) targetFormat.format(date) else ""
    }

}
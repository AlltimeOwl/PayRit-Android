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

}
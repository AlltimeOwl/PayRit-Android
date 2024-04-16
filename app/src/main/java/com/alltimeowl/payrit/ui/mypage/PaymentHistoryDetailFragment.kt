package com.alltimeowl.payrit.ui.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentPaymentHistoryDetailBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentHistoryDetailFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPaymentHistoryDetailBinding

    private lateinit var viewModel: PaymentHistoryViewModel

    private var historyId: Int = 0

    val TAG = "PaymentHistoryDetailFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentPaymentHistoryDetailBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[PaymentHistoryViewModel::class.java]

        historyId = arguments?.getInt("historyId")!!

        val accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.getTransactionDetail(accessToken, historyId)

        initUI()
        observeData()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            materialToolbarPaymentHistoryDetail.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PAYMENT_HISTORY_DETAIL_FRAGMENT)
                }
            }
        }
    }
    private fun observeData() {
        binding.run {
            viewModel.transactionDetail.observe(viewLifecycleOwner) { transactionInfo ->
                Log.d(TAG, "transactionInfo : ${transactionInfo}")

                // 날짜 포맷 변경
                val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                val targetFormat = SimpleDateFormat("yyyy.MM.dd.HH:mm:ss", Locale.getDefault())
                val date = originalFormat.parse(transactionInfo.transactionDate)
                val formattedDate = date?.let { targetFormat.format(it) } ?: ""

                textViewApprovalDatePaymentHistoryDetail.text = formattedDate
                textViewApprovalNumberPaymentHistoryDetail.text = transactionInfo.applyNum
                textViewPaymentMethodPaymentHistoryDetail.text = transactionInfo.paymentMethod
                textViewProductAmountPaymentHistoryDetail.text = mainActivity.convertMoneyFormat(transactionInfo.amount) + "원"
                textViewTotalAmountPaymentHistoryDetail.text = mainActivity.convertMoneyFormat(transactionInfo.amount) + "원"
            }
        }
    }

}
package com.alltimeowl.payrit.ui.approval

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentRecipientApprovalBinding
import com.alltimeowl.payrit.ui.home.HomeViewModel
import com.alltimeowl.payrit.ui.main.MainActivity


class RecipientApprovalFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentRecipientApprovalBinding

    private lateinit var viewModel: HomeViewModel

    private var paperId: Int = 0

    val TAG = "RecipientApprovalFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentRecipientApprovalBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        paperId = arguments?.getInt("paperId")!!

        MainActivity.accessToken?.let { it1 -> viewModel.getIouDetail(it1, paperId) }

        initUI()
        observeData()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            mainActivity.hideBottomNavigationView()

            materialToolbarRecipientApproval.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.RECIPIENT_APPROVAL_FRAGMENT)
                }
            }

        }
    }

    private fun observeData() {
        viewModel.iouDetail.observe(viewLifecycleOwner) { iouDetailInfo ->

            binding.progressBarRecipientApproval.visibility = View.GONE
            binding.scrollViewRecipientApproval.visibility = View.VISIBLE

            // 거래 내역
            binding.textViewTransactionAmountRecipientApproval.text = mainActivity.convertMoneyFormat(iouDetailInfo.amount) + "원"
            binding.textViewTransactionDateRecipientApproval.text = mainActivity.convertDateFormat(iouDetailInfo.repaymentEndDate)

            // 추가 사항
            if (iouDetailInfo.interestRate == 0 && iouDetailInfo.interestPaymentDate == 0 && iouDetailInfo.specialConditions.isEmpty()) {
                binding.textViewAdditionContractTitleRecipientApproval.visibility = View.GONE
                binding.cardViewAdditionContractRecipientApproval.visibility = View.GONE
            } else {

                // 이자율
                if (iouDetailInfo.interestRate == 0) {
                    binding.linearLayoutAdditionContractInterestRateRecipientApproval.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractInterestRateRecipientApproval.text = "${iouDetailInfo.interestRate}%"
                }

                // 이자 지급일
                if (iouDetailInfo.interestPaymentDate == 0) {
                    binding.linearLayoutAdditionContractDateRecipientApproval.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractDateRecipientApproval.text = "매월 ${iouDetailInfo.interestPaymentDate}일"
                }

                // 특약사항
                if (iouDetailInfo.specialConditions.isEmpty()) {
                    binding.linearLayoutAdditionContractRecipientApproval.visibility = View.GONE
                } else {
                    binding.textViewAdditionContractRecipientApproval.text = iouDetailInfo.specialConditions
                }

            }

            // 빌려준 사람
            binding.textViewLendPersonNameRecipientApproval.text = iouDetailInfo.creditorName
            binding.textViewLendPersonPhoneNumberRecipientApproval.text = mainActivity.convertPhoneNumber(iouDetailInfo.creditorPhoneNumber)
            if (iouDetailInfo.creditorAddress.isEmpty()) {
                binding.linearLayoutLendPersonAddressRecipientApproval.visibility = View.GONE
            } else {
                binding.textViewLendPersonAddressRecipientApproval.text = iouDetailInfo.creditorAddress
            }

            // 빌린 사람
            binding.textViewBorrowPersonNameRecipientApproval.text = iouDetailInfo.debtorName
            binding.textViewBorrowPersonPhoneNumberRecipientApproval.text = mainActivity.convertPhoneNumber(iouDetailInfo.debtorPhoneNumber)
            if (iouDetailInfo.debtorAddress.isEmpty()) {
                binding.linearLayoutBorrowPersonAddressRecipientApproval.visibility = View.GONE
            } else {
                binding.textViewBorrowPersonAddressRecipientApproval.text = iouDetailInfo.debtorAddress
            }

        }
    }

}
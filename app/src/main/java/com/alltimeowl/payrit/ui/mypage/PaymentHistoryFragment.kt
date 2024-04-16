package com.alltimeowl.payrit.ui.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentPaymentHistoryBinding
import com.alltimeowl.payrit.ui.main.MainActivity


class PaymentHistoryFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPaymentHistoryBinding
    private lateinit var spinnerAdapter: PaymentHistorySpinnerAdapter

    private lateinit var viewModel: PaymentHistoryViewModel

    val TAG = "PaymentHistoryFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentPaymentHistoryBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[PaymentHistoryViewModel::class.java]

        val accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.getMyTransactionList(accessToken)

        initUI()
        observeData()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            mainActivity.hideBottomNavigationView()

            materialToolbarPaymentHistory.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PAYMENT_HISTORY_FRAGMENT)
                }
            }

            val category = resources.getStringArray(R.array.payment_history)

            spinnerPaymentHistory.run {
                spinnerAdapter = PaymentHistorySpinnerAdapter(mainActivity, R.layout.item_payment_history_spinner, category)
                adapter = spinnerAdapter

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                        viewModel.transactionList.value?.let { list ->
                            val sortedList = when (position) {
                                0 -> list.sortedByDescending {it.transactionDate}
                                1 -> list.sortedBy { it.transactionDate }

                                else -> list
                            }

                            (binding.recyclerViewPaymentHistory.adapter as PaymentHistoryAdapter).updateData(sortedList)

                            if (sortedList.isEmpty()) {
                                binding.linearLayoutExistencePaymentHistory.visibility = View.GONE
                            } else {
                                binding.linearLayoutExistencePaymentHistory.visibility = View.VISIBLE
                            }

                        }

                        if (position == 0) {
                            spinnerAdapter.isDropdownOpen = true
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        spinnerAdapter.isDropdownOpen= false
                    }
                }

            }

            recyclerViewPaymentHistory.run {
                recyclerViewPaymentHistory.layoutManager = LinearLayoutManager(context)
                adapter = PaymentHistoryAdapter(mainActivity, mutableListOf())
            }

        }

    }

    private fun observeData() {
        viewModel.transactionList.observe(viewLifecycleOwner) { transactionList ->

            val sortedList = transactionList.sortedByDescending { it.transactionDate }

            binding.progressBarLoadingPaymentHistory.visibility = View.GONE

            (binding.recyclerViewPaymentHistory.adapter as PaymentHistoryAdapter).updateData(sortedList)

            if (sortedList.isEmpty()) {
                binding.linearLayoutExistencePaymentHistory.visibility = View.GONE
            } else {
                binding.linearLayoutExistencePaymentHistory.visibility = View.VISIBLE
            }

        }
    }

}
package com.alltimeowl.payrit.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentPaymentHistoryBinding
import com.alltimeowl.payrit.ui.main.MainActivity


class PaymentHistoryFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPaymentHistoryBinding
    private lateinit var spinnerAdapter: PaymentHistorySpinnerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentPaymentHistoryBinding.inflate(layoutInflater)

        initUI()

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
                adapter = PaymentHistoryAdapter(mainActivity)
            }

        }

    }

}
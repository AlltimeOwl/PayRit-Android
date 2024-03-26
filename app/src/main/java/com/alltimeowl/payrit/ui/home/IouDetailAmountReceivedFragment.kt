package com.alltimeowl.payrit.ui.home

import android.app.DatePickerDialog
import android.icu.text.NumberFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.RepaymentRequest
import com.alltimeowl.payrit.databinding.FragmentIouDetailAmountReceivedBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import java.util.Locale

class IouDetailAmountReceivedFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouDetailAmountReceivedBinding

    private lateinit var viewModel: HomeViewModel

    private var paperId = 0
    private var repaymentDate = ""
    private var repaymentAmount = 0

    val TAG = "IouDetailAmountReceivedFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouDetailAmountReceivedBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        paperId = arguments?.getInt("paperId", paperId)!!

        MainActivity.accessToken?.let { viewModel.getIouDetail(it, paperId) }

        initUI()
        observeData()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            materialToolbarIouDetailAmountReceived.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_DETAIL_AMOUNT_RECEIVED_FRAGMENT)
                }
            }

            // 금액
            editTextAmountIouDetailAmountReceived.addTextChangedListener(
                getTextWatcher(editTextAmountIouDetailAmountReceived)
            )

            // 받은 날짜
            textInputLayoutDateIouDetailAmountReceived.setEndIconOnClickListener {

                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                context?.let { it1 ->
                    DatePickerDialog(it1, { _, year, month, day ->
                        run {
                            binding.editTextDateIouDetailAmountReceived.setText(year.toString() + "." + (month + 1).toString() + "." + day.toString())
                            repaymentDate  = String.format("%d-%02d-%02d", year, month + 1, day)
                        }
                    }, year, month, day)
                }?.show()

            }

            recyclerViewIouDetailAmountReceived.run {
                recyclerViewIouDetailAmountReceived.layoutManager = LinearLayoutManager(context)
                adapter = IouDetailAmountReceivedAdapter(mainActivity, mutableListOf())
            }

            buttonInputIouDetailAmountReceived.setOnClickListener {
                inputRepayment()
            }

        }
    }

    private fun getTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString()

                // 콤마를 제거한 깨끗한 문자열을 생성합니다.
                val cleanString = text.replace(",", "")

                // 빈 문자열을 체크하고, 빈 문자열이 아닐 때만 정수로 변환합니다.
                if (cleanString.isNotEmpty()) {
                    try {
                        // 콤마가 제거된 문자열을 이용하여 repaymentAmount를 설정합니다.
                        repaymentAmount = cleanString.toInt()
                    } catch (e: NumberFormatException) {
                        Log.d(TAG, "NumberFormatException for input string: \"$cleanString\"", e)
                        // 올바른 정수 값을 설정하거나 사용자에게 입력 오류를 알리는 등의 처리를 할 수 있습니다.
                    }
                } else {
                    // 빈 문자열일 경우, repaymentAmount를 0 또는 기본값으로 설정할 수 있습니다.
                    repaymentAmount = 0
                }

                val parsed = cleanString.toDoubleOrNull() ?: 0.0
                val formatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(parsed)
                editText.removeTextChangedListener(this)
                editText.setText(formatted)
                editText.setSelection(formatted.length)
                editText.addTextChangedListener(this)
            }

        }
    }

    private fun inputRepayment() {
        if (repaymentDate.isNotEmpty() && repaymentAmount !=0) {
            val repaymentRequest = RepaymentRequest(paperId, repaymentDate, repaymentAmount)
            MainActivity.accessToken?.let { viewModel.postRepayment(it, repaymentRequest, paperId) }
        } else {
            return
        }
    }

    private fun observeData() {
        viewModel.repaymentList.observe(viewLifecycleOwner) { repaymentList->
            // repaymentDate를 기준으로 내림차순 정렬
            val sortedList = repaymentList.sortedByDescending { it.repaymentDate }
            (binding.recyclerViewIouDetailAmountReceived.adapter as IouDetailAmountReceivedAdapter).updateData(sortedList)
        }
    }

}
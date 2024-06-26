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
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentIouDetailAmountReceivedBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import java.util.Locale

class IouDetailAmountReceivedFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouDetailAmountReceivedBinding

    private lateinit var viewModel: HomeViewModel

    private var paperId = 0
    private var remainingAmount = 0
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

        val accessToken = SharedPreferencesManager.getAccessToken()
        viewModel.getIouDetail(accessToken, paperId)

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
                adapter = IouDetailAmountReceivedAdapter(mainActivity, viewModel, paperId, mutableListOf())
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

                if (cleanString.isNotEmpty()) {
                    try {
                        // 콤마가 제거된 문자열을 이용하여 repaymentAmount를 설정합니다.
                        val inputAmount = cleanString.toInt()

                        // 입력된 값이 remainingAmount보다 큰지 확인합니다.
                        if (inputAmount > remainingAmount) {
                            // remainingAmount로 repaymentAmount를 설정합니다.
                            repaymentAmount = remainingAmount

                            // remainingAmount 값을 문자열로 변환하고, 콤마를 추가합니다.
                            val formattedRemainingAmount = NumberFormat.getNumberInstance(Locale.getDefault()).format(remainingAmount)
                            editText.removeTextChangedListener(this)
                            editText.setText(formattedRemainingAmount)
                            editText.setSelection(formattedRemainingAmount.length)
                            editText.addTextChangedListener(this)
                        } else {
                            // 입력된 값이 remainingAmount보다 작거나 같을 경우 정상 처리합니다.
                            repaymentAmount = inputAmount

                            val parsed = cleanString.toDoubleOrNull() ?: 0.0
                            val formatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(parsed)
                            editText.removeTextChangedListener(this)
                            editText.setText(formatted)
                            editText.setSelection(formatted.length)
                            editText.addTextChangedListener(this)
                        }
                    } catch (e: NumberFormatException) {
                        Log.d(TAG, "NumberFormatException for input string: \"$cleanString\"", e)
                        // 입력값 오류 처리
                    }
                } else {
                    // 빈 문자열일 경우 처리
                    repaymentAmount = 0
                    editText.removeTextChangedListener(this)
                    editText.setText("")
                    editText.addTextChangedListener(this)
                }
            }

        }
    }

    private fun inputRepayment() {
        if (repaymentDate.isNotEmpty() && repaymentAmount !=0) {
            val repaymentRequest = RepaymentRequest(paperId, repaymentDate, repaymentAmount)
            val accessToken = SharedPreferencesManager.getAccessToken()
            viewModel.postRepayment(accessToken, repaymentRequest, paperId)
            binding.editTextAmountIouDetailAmountReceived.text?.clear()
            binding.editTextAmountIouDetailAmountReceived.clearFocus()
        } else {
            return
        }
    }

    private fun observeData() {
        viewModel.iouDetail.observe(viewLifecycleOwner) { iou ->
            remainingAmount = iou.paperFormInfo.remainingAmount
        }

        viewModel.repaymentList.observe(viewLifecycleOwner) { repaymentList->
            // repaymentDate를 기준으로 내림차순 정렬
            val sortedList = repaymentList.sortedByDescending { it.repaymentDate }
            (binding.recyclerViewIouDetailAmountReceived.adapter as IouDetailAmountReceivedAdapter).updateData(sortedList)
        }
    }

}
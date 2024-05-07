package com.alltimeowl.payrit.ui.promise

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.PromiseData
import com.alltimeowl.payrit.databinding.FragmentPromiseInformationBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.Locale

class PromiseInformationFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPromiseInformationBinding

    private var promiseStartDate: Calendar? = null

    private var promiseDataList: ArrayList<PromiseData>? = null

    val TAG = "PromiseInformationFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentPromiseInformationBinding.inflate(layoutInflater)

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

            materialToolbarPromiseInformation.run {

                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PROMISE_INFORMATION_FRAGMENT)
                }

                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.item_cancel -> mainActivity.showCancelAlertDialog()
                    }
                    false
                }
            }

            settingFunction()
        }
    }

    private fun settingFunction() {
        binding.run {

            // 약속 날짜
            textInputLayoutStartPromiseInformation.setEndIconOnClickListener {
                showDatePicker(editTextStartPromiseInformation, true)
            }

            // 약속 마감일
            textInputLayoutDeadlinePromiseInformation.setEndIconOnClickListener {
                promiseStartDate?.let {
                    showDatePicker(editTextDeadlinePromiseInformation, false)
                }
            }

            validationButton()
        }
    }

    private fun showDatePicker(editText: EditText, isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)

            if (isStartDate) {
                promiseStartDate = selectedDate
                // 약속 시작 날짜를 설정한 후 마감일이 약속 시작 날짜보다 이전인지 확인
                val deadlineText = binding.editTextDeadlinePromiseInformation.text.toString()
                if (deadlineText.isNotEmpty()) {
                    val deadlineDate = SimpleDateFormat("yyyy.M.d", Locale.getDefault()).parse(deadlineText)
                    deadlineDate?.let {
                        val deadlineCalendar = Calendar.getInstance()
                        deadlineCalendar.time = it
                        if (promiseStartDate!!.after(deadlineCalendar)) {
                            // 마감일이 약속 시작 날짜보다 이전이면 마감일 텍스트를 지움
                            binding.editTextDeadlinePromiseInformation.setText("")
                        }
                    }
                }
            } else {
                // 마감일을 설정하는 경우
                editText.setText("$selectedYear.${selectedMonth + 1}.$selectedDay")
            }

            // 시작 날짜 설정 시 텍스트 설정
            if (isStartDate) {
                editText.setText("$selectedYear.${selectedMonth + 1}.$selectedDay")
            }

        }, year, month, day)

        if (!isStartDate) {
            // 약속 마감일 선택 시, 약속 시작 날짜를 최소 날짜로 설정
            promiseStartDate?.let {
                datePickerDialog.datePicker.minDate = it.timeInMillis
            }
        } else {
            // 약속 시작 날짜 선택 시, 현재 날짜를 최소 날짜로 설정
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        }

        datePickerDialog.show()
    }

    private fun validationButton() {
        binding.run {

            val editTextAmount = editTextAmountPromiseInformation
            val editTextStart = editTextStartPromiseInformation
            val editTextDeadline = editTextDeadlinePromiseInformation
            val editTextContents = editTextContentsPromiseInformation

            fun validateFields() {
                val amount = editTextAmount.text.toString()
                val start = editTextStart.text.toString()
                val deadline = editTextDeadline.text.toString()
                val contents = editTextContents.text.toString()

                if (amount.isNotEmpty() && start.isNotEmpty() && deadline.isNotEmpty() && contents.isNotEmpty()) {
                    buttonNextPromiseInformation.setBackgroundResource(R.drawable.bg_primary_mint_r12)

                    buttonNextPromiseInformation.setOnClickListener {

                        val bundle = Bundle()
                        bundle.putString("amount", amount)
                        bundle.putString("promiseStartDate", start)
                        bundle.putString("promiseEndDate", deadline)
                        bundle.putString("contents", contents)
                        bundle.putParcelableArrayList("promiseDataList", promiseDataList)

                        mainActivity.replaceFragment(MainActivity.PROMISE_MAKE_FRAGMENT, true, bundle)
                    }

                } else {
                    buttonNextPromiseInformation.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
                }
            }

            editTextAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {

                    // 입력된 값이 null이 아니고, 길이가 2 이상이며, '0'으로 시작하는 경우
                    if (s != null && s.startsWith("0") && s.length > 1) {
                        // '0' 다음의 숫자부터 시작하는 부분 문자열을 취득
                        val correctedString = s.substring(1)

                        // 무한 루프를 방지하기 위해 TextWatcher를 일시적으로 제거
                        editTextAmount.removeTextChangedListener(this)

                        // 수정된 문자열로 EditText를 업데이트
                        editTextAmount.setText(correctedString)

                        // 커서를 문자열 끝으로 이동
                        editTextAmount.setSelection(correctedString.length)

                        // TextWatcher를 다시 추가
                        editTextAmount.addTextChangedListener(this)
                    }

                    validateFields()
                }
            })

            editTextStart.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    validateFields()
                }
            })

            editTextDeadline.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    validateFields()
                }
            })

            editTextContents.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    validateFields()
                }
            })

        }
    }

}
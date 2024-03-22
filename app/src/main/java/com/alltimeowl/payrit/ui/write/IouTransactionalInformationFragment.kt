package com.alltimeowl.payrit.ui.write

import android.app.DatePickerDialog
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.icu.text.SimpleDateFormat
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
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentIouTransactionalInformationBinding
import com.alltimeowl.payrit.databinding.ItemCancelBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.ParseException
import java.util.Locale

class IouTransactionalInformationFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouTransactionalInformationBinding

    private lateinit var writerRole: String

    val TAG = "IouTransactionalInformationFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainActivity = activity as MainActivity
        binding = FragmentIouTransactionalInformationBinding.inflate(layoutInflater)

        writerRole = arguments?.getString("writerRole").toString()

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            materialToolbarIouTransactionalInformation.run {

                // 뒤로가기 버튼
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_TRANSACTIONAL_INFORMATION_FRAGMENT)
                }

                // X 버튼
                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.item_cancel -> showAlertDialog()
                    }
                    false
                }

            }

            // 금액 입력
            editTextAmountIouTransactionalInformation.addTextChangedListener(
                getTextWatcher(editTextAmountIouTransactionalInformation)
            )

            // 빌려준 날
            textInputLayoutStartIouTransactionalInformation.setEndIconOnClickListener {
                showDatePicker(binding.editTextStartIouTransactionalInformation)
            }

            // 갚기로 한 날
            textInputLayoutDeadlineIouTransactionalInformation.setEndIconOnClickListener {
                showDatePicker(binding.editTextDeadlineIouTransactionalInformation)
            }

            // 날짜 입력에 대한 각 EditText 에 대한 TextWatcher 설정
            editTextStartIouTransactionalInformation.addTextChangedListener(
                getDateWatcher()
            )

            editTextDeadlineIouTransactionalInformation.addTextChangedListener(
                getDateWatcher()
            )

            // 이자율 안내서 클릭
            includeIouTransactionalInformation.imageViewQuestionLayoutInterestOn.setOnClickListener {
                mainActivity.hideKeyboard()

                val inflater: LayoutInflater = LayoutInflater.from(context)
                val layout: View = inflater.inflate(R.layout.toast_message_interest_rate_guide, null)

                val toast = Toast(context)
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.show()

            }

            // 이자율 입력
            includeIouTransactionalInformation.editTextInterestRateLayoutInterestOn.addTextChangedListener(
                getInterestRateWatcher()
            )

            // 이자 지급일(선택) 입력
            includeIouTransactionalInformation.editTextPaymentDateLayoutInterestOn.addTextChangedListener(
                getPaymentDateWatcher()
            )

            val includeView = binding.root.findViewById<View>(R.id.include_iou_transactional_information)

            val layoutParams = includeView.layoutParams as LinearLayoutCompat.LayoutParams

            // 처음에 금액을 입력을 안 한 상태에서 이자 계산을 open 했을 때
            // - marginBottom 80을 줌
            // - 그래야 다음 버튼에 안 가려짐

            // dp 값을 px로 변환
            val dpValue = 80 // dp
            val scale = includeView.resources.displayMetrics.density
            val pxValue = (dpValue * scale + 0.5f).toInt() // dp 값을 px로 변환

            if (linearLayoutSummaryIouTransactionalInformation.visibility == View.GONE) {
                layoutParams.bottomMargin = pxValue // 마진 바텀 값을 px로 설정
            } else {
                layoutParams.bottomMargin = 0 // 마진 바텀 값을 0으로 설정
            }

            includeView.layoutParams = layoutParams // 변경된 레이아웃 파라미터를 다시 설정

            // switch
            switchIouTransactionalInformation.setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
                    linearLayoutInterestOffIouTransactionalInformation.visibility = View.GONE
                    includeView.visibility = View.VISIBLE

                } else {
                    linearLayoutInterestOffIouTransactionalInformation.visibility = View.VISIBLE
                    includeView.visibility = View.GONE
                    includeIouTransactionalInformation.switchLayoutInterestOn.isChecked = true
                }

                validationButton()
            }

            includeIouTransactionalInformation.switchLayoutInterestOn.setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
                    linearLayoutInterestOffIouTransactionalInformation.visibility = View.GONE
                    includeView.visibility = View.VISIBLE
                } else {
                    linearLayoutInterestOffIouTransactionalInformation.visibility = View.VISIBLE
                    includeView.visibility = View.GONE
                    switchIouTransactionalInformation.isChecked = false
                }

                validationButton()
            }

            validationButton()
        }
    }

    // 금액 입력 텍스트 변화 감지기를 반환하는 함수
    private fun getTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()

                val includeView = binding.root.findViewById<View>(R.id.include_iou_transactional_information)

                val layoutParams = includeView.layoutParams as LinearLayoutCompat.LayoutParams

                val dpValue = 80
                val scale = includeView.resources.displayMetrics.density
                val pxValue = (dpValue * scale + 0.5f).toInt()

                includeView.layoutParams = layoutParams

                if (text.isEmpty()) {
                    binding.linearLayoutSummaryIouTransactionalInformation.visibility = View.GONE
                    layoutParams.bottomMargin = pxValue
                    binding.includeIouTransactionalInformation.editTextInterestRateLayoutInterestOn.text?.clear()
                    binding.includeIouTransactionalInformation.textInputLayoutInterestRateLayoutInterestOn.error = null
                } else {
                    layoutParams.bottomMargin = 0
                    binding.includeIouTransactionalInformation.editTextInterestRateLayoutInterestOn.text?.clear()
                    binding.includeIouTransactionalInformation.textInputLayoutInterestRateLayoutInterestOn.error = null
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString()
                val cleanString = text.replace("[^\\d]".toRegex(), "")
                val parsed = cleanString.toDoubleOrNull()
                if (parsed != null) {
                    val formatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(parsed)
                    editText.removeTextChangedListener(this)
                    editText.setText(formatted)
                    editText.setSelection(formatted.length)
                    editText.addTextChangedListener(this)

                    // EditText에 입력이 있으면 summary layout을 보이게 함
                    binding.linearLayoutSummaryIouTransactionalInformation.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE

                    // 입력된 금액을 TextView에 설정
                    binding.textTotalAmountIouTransactionalInformation.text = if (text.isNotEmpty()) formatted + "원" else ""
                }
            }
        }
    }

    // 날짜를 감시
    private fun getDateWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.includeIouTransactionalInformation.editTextInterestRateLayoutInterestOn.text?.clear()
                binding.includeIouTransactionalInformation.textInputLayoutInterestRateLayoutInterestOn.error = null

                val startDateText = binding.editTextStartIouTransactionalInformation.text.toString()
                val deadlineDateText = binding.editTextDeadlineIouTransactionalInformation.text.toString()

                if (startDateText.isNotEmpty() && deadlineDateText.isNotEmpty()) {
                    val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                    val startDate = dateFormat.parse(startDateText)
                    val deadlineDate = dateFormat.parse(deadlineDateText)

                    if (startDate != null && deadlineDate != null) {
                        if (deadlineDate <= startDate) {
                            binding.includeIouTransactionalInformation.editTextInterestRateLayoutInterestOn.text?.clear()
                            binding.includeIouTransactionalInformation.textInputLayoutInterestRateLayoutInterestOn.error = null
                        }
                    }
                } else if (startDateText.isEmpty() || deadlineDateText.isEmpty()) {
                    binding.includeIouTransactionalInformation.editTextInterestRateLayoutInterestOn.text?.clear()
                }

            }

            override fun afterTextChanged(editable: Editable?) {
                val startDateText = binding.editTextStartIouTransactionalInformation.text.toString()
                val deadlineDateText = binding.editTextDeadlineIouTransactionalInformation.text.toString()

                if (startDateText.isNotEmpty() && deadlineDateText.isNotEmpty()) {
                    val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                    val startDate = dateFormat.parse(startDateText)
                    val deadlineDate = dateFormat.parse(deadlineDateText)

                    if (startDate != null && deadlineDate != null) {
                        if (deadlineDate <= startDate) {
                            // 마감 날짜가 시작 날짜 보다 같거나 이전인 경우 에러 메시지 표시
                            binding.textViewErrorMessageIouTransactionalInformation.visibility = View.VISIBLE
                        } else {
                            binding.textViewErrorMessageIouTransactionalInformation.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    // 날짜 설정을 위한 DatePickerDialog 표시
    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, day)

            editText.setText("$year.${month + 1}.$day")
        }, year, month, day)

        // 선택 가능한 최소 날짜를 현재 날짜로 설정
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    // 연 이자율 텍스트 변화 감지기를 반환하는 함수
    private fun getInterestRateWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (text.isEmpty()) {
                    binding.includeIouTransactionalInformation.textViewInterestAmountLayoutInterestOn.text = "0"

                    if (binding.linearLayoutSummaryIouTransactionalInformation.visibility == View.VISIBLE) {
                        val amountText = binding.editTextAmountIouTransactionalInformation.text.toString()
                        binding.textTotalAmountIouTransactionalInformation.text = amountText
                    }

                }
            }

            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString()
                val interestRate = text.toDoubleOrNull() ?: return // If conversion fails, return early

                // 입력된 값이 소수점 아래의 두 자리까지만 입력되도록 제한
                if (text.contains(".")) {
                    val splitText = text.split(".")
                    if (splitText.size > 1 && splitText[1].length > 2) {
                        // 소수점 이하의 자릿수가 2자리를 넘으면 마지막 자리를 제거합니다.
                        val newText = "${splitText[0]}.${splitText[1].substring(0, 2)}"
                        binding.includeIouTransactionalInformation.editTextInterestRateLayoutInterestOn.setText(newText)
                        binding.includeIouTransactionalInformation.editTextInterestRateLayoutInterestOn.setSelection(newText.length)
                        return
                    }
                }

                // 입력된 값이 20을 초과하는지 확인합니다.
                if (interestRate > 20) {

                    mainActivity.hideKeyboard()

                    val inflater: LayoutInflater = LayoutInflater.from(context)
                    val layout: View = inflater.inflate(R.layout.toast_message_interest_rate, null)

                    val toast = Toast(context)
                    toast.duration = Toast.LENGTH_LONG
                    toast.view = layout
                    toast.show()

                    val amountText = binding.editTextAmountIouTransactionalInformation.text.toString()
                    binding.textTotalAmountIouTransactionalInformation.text = "$amountText 원"

                    binding.includeIouTransactionalInformation.editTextInterestRateLayoutInterestOn.text?.clear()
                    binding.includeIouTransactionalInformation.textViewInterestAmountLayoutInterestOn.text = "0"

                    binding.buttonNextIouTransactionalInformation.setOnClickListener(null)

                    binding.includeIouTransactionalInformation.textInputLayoutInterestRateLayoutInterestOn.error = "이자는 20%를 넘어설 수 없어요."
                } else {
                    binding.includeIouTransactionalInformation.textInputLayoutInterestRateLayoutInterestOn.error = null
                    calculateInterest()
                }

            }

            // 연 이자율 계산
            private fun calculateInterest() {
                val amountText = binding.editTextAmountIouTransactionalInformation.text.toString().replace(",", "")
                val startText = binding.editTextStartIouTransactionalInformation.text.toString()
                val deadlineText = binding.editTextDeadlineIouTransactionalInformation.text.toString()
                val interestRateText = binding.includeIouTransactionalInformation.editTextInterestRateLayoutInterestOn.text.toString()

                // 입력된 값이 없으면 함수를 종료합니다.
                if (amountText.isEmpty() || startText.isEmpty() || deadlineText.isEmpty() || interestRateText.isEmpty()) {
                    return
                }

                val interestRate = interestRateText.toDoubleOrNull() ?: return
                val amount = amountText.toDouble()

                // 날짜 파싱
                val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                val startDate = dateFormat.parse(startText)
                val deadlineDate = dateFormat.parse(deadlineText)

                if (deadlineDate <= startDate) {
                    return
                }

                // 날짜 차이 계산
                val diff = deadlineDate.time - startDate.time

                val days = diff / (24 * 60 * 60 * 1000) + 1
                val daysInYear = 365
                val interest = amount * (interestRate / 100) / daysInYear * days

                // 결과를 TextView에 표시합니다.
                val decimalFormat = DecimalFormat("#,##0.00")
                val formattedInterest = decimalFormat.format(interest)
                binding.includeIouTransactionalInformation.textViewInterestAmountLayoutInterestOn.text = formattedInterest

                // 합계 계산
                val totalAmountWithoutDecimal = (amount + interest).toInt()
                val formattedTotalAmount = NumberFormat.getNumberInstance(Locale.getDefault()).format(totalAmountWithoutDecimal)
                binding.textTotalAmountIouTransactionalInformation.text = "$formattedTotalAmount 원"
            }

        }
    }

    // 이자 지급일(선택) 텍스트 변화 감지기를 반환하는 함수
    private fun getPaymentDateWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().replace("일", "")

                if (input.isEmpty()) {
                    binding.includeIouTransactionalInformation.textInputLayoutPaymentDateLayoutInterestOn.error = null
                } else {
                    val intValue = input.toIntOrNull()
                    if (intValue != null && intValue in 1..31) {
                        val formattedInput = "${intValue}일"
                        if (formattedInput != s.toString()) {
                            binding.includeIouTransactionalInformation.editTextPaymentDateLayoutInterestOn.setText(formattedInput)
                            // 커서를 마지막 위치로 이동
                            binding.includeIouTransactionalInformation.editTextPaymentDateLayoutInterestOn.setSelection(formattedInput.length)
                        }
                        binding.includeIouTransactionalInformation.textInputLayoutPaymentDateLayoutInterestOn.error = null
                    } else {
                        binding.includeIouTransactionalInformation.textInputLayoutPaymentDateLayoutInterestOn.error = "날짜를 제대로 입력해주세요"
                    }
                }
            }
        }
    }


    private fun validationButton() {
        binding.run {

            val editTextAmount = editTextAmountIouTransactionalInformation
            val editTextStart = editTextStartIouTransactionalInformation
            val editTextDeadline = editTextDeadlineIouTransactionalInformation
            val editTextSignificant = editTextSignificantIouTransactionalInformation
            val editTextRage = includeIouTransactionalInformation.editTextInterestRateLayoutInterestOn
            val editTextPaymentDate = includeIouTransactionalInformation.editTextPaymentDateLayoutInterestOn
            val buttonNext = buttonNextIouTransactionalInformation

            fun validateFields() {

                val amount = editTextAmount.text.toString()
                val start = editTextStart.text.toString()
                val deadline = editTextDeadline.text.toString()
                val significant = editTextSignificant.text.toString()
                val rageText = editTextRage.text.toString()
                val paymentDate = editTextPaymentDate.text.toString()

                val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                val iouDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val today = iouDateFormat.format(Calendar.getInstance().time)

                val rage = if (rageText.isNotEmpty() && rageText[0] != '.') rageText.toDouble() else 0.0

                if (amount.isNotEmpty() && start.isNotEmpty() && deadline.isNotEmpty()) {
                    try {
                        val startDate = dateFormat.parse(start)
                        val deadlineDate = dateFormat.parse(deadline)

                        val amountWithoutComma = amount.replace(",", "")
                        // 파싱된 Date 객체를 원하는 형식으로 다시 포맷
                        val repaymentStartDate = startDate?.let { iouDateFormat.format(it) }
                        val repaymentEndDate = deadlineDate?.let { iouDateFormat.format(it) }

                        val paymentDateWithoutDay = paymentDate.replace("일","")
                        // 비어있는지 확인하고 TextInputLayout의 오류 상태도 확인
                        val paymentDateInt: Int? = if (paymentDateWithoutDay.isNotEmpty() &&
                            includeIouTransactionalInformation.textInputLayoutPaymentDateLayoutInterestOn.error == null) {
                            paymentDateWithoutDay.toInt()
                        } else {
                            null
                        }

                        if (startDate != null && deadlineDate != null && startDate.before(deadlineDate)) {
                            buttonNext.setBackgroundResource(R.drawable.bg_primary_mint_r12)

                            buttonNext.setOnClickListener {
                                if (!switchIouTransactionalInformation.isChecked) {

                                    val totalAmountIou = textTotalAmountIouTransactionalInformation.text
                                    val numberOnlyTotalAmountIou = totalAmountIou.replace(Regex("[^\\d]"), "")

                                    val bundle = Bundle()
                                    bundle.putString("writerRole", writerRole)
                                    bundle.putInt("amount", amountWithoutComma.toInt())
                                    bundle.putInt("calcedAmount", numberOnlyTotalAmountIou.toInt())
                                    bundle.putString("transactionDate", today)
                                    bundle.putString("repaymentStartDate", repaymentStartDate.toString())
                                    bundle.putString("repaymentEndDate", repaymentEndDate.toString())
                                    bundle.putString("specialConditions", significant)
                                    bundle.putFloat("interestRate", rage.toFloat())
                                    if (paymentDateInt != null) {
                                        bundle.putInt("interestPaymentDate", paymentDateInt)
                                    }

                                    mainActivity.replaceFragment(MainActivity.IOU_WRITE_MY_FRAGMENT, true, bundle)
                                }
                                return@setOnClickListener
                            }

                            return
                        }
                    } catch (e: ParseException) {
                        Log.d(TAG, "e.printStackTrace() : ${e.printStackTrace()}")
                    }
                }

                buttonNext.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
                buttonNext.setOnClickListener(null)

            }

            fun rateValidateFields() {

                val amount = editTextAmount.text.toString()
                val start = editTextStart.text.toString()
                val deadline = editTextDeadline.text.toString()
                val significant = editTextSignificant.text.toString()
                val rageText = editTextRage.text.toString()
                val paymentDate = editTextPaymentDate.text.toString()

                val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                val iouDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val today = iouDateFormat.format(Calendar.getInstance().time)

                val rage = if (rageText.isNotEmpty() && rageText[0] != '.') rageText.toDouble() else 0.0

                if (amount.isNotEmpty() && start.isNotEmpty() && deadline.isNotEmpty() && rage in 0.01..20.0) {
                    try {
                        val startDate = dateFormat.parse(start)
                        val deadlineDate = dateFormat.parse(deadline)

                        val amountWithoutComma = amount.replace(",", "")
                        // 파싱된 Date 객체를 원하는 형식으로 다시 포맷
                        val repaymentStartDate = startDate?.let { iouDateFormat.format(it) }
                        val repaymentEndDate = deadlineDate?.let { iouDateFormat.format(it) }

                        val paymentDateWithoutDay = paymentDate.replace("일","")
                        // 비어있는지 확인하고 TextInputLayout의 오류 상태도 확인
                        val paymentDateInt: Int? = if (paymentDateWithoutDay.isNotEmpty() &&
                            includeIouTransactionalInformation.textInputLayoutPaymentDateLayoutInterestOn.error == null) {
                            paymentDateWithoutDay.toInt()
                        } else {
                            null // null을 허용하는 Int? 타입으로 처리
                        }

                        if (startDate != null && deadlineDate != null && startDate.before(deadlineDate)) {
                            buttonNext.setBackgroundResource(R.drawable.bg_primary_mint_r12)

                            buttonNext.setOnClickListener {
                                if (includeIouTransactionalInformation.switchLayoutInterestOn.isChecked) {

                                    val totalAmountIou = textTotalAmountIouTransactionalInformation.text
                                    val numberOnlyTotalAmountIou = totalAmountIou.replace(Regex("[^\\d]"), "")

                                    val bundle = Bundle()
                                    bundle.putString("writerRole", writerRole)
                                    bundle.putInt("amount", amountWithoutComma.toInt())
                                    bundle.putInt("calcedAmount", numberOnlyTotalAmountIou.toInt())
                                    bundle.putString("transactionDate", today)
                                    bundle.putString("repaymentStartDate", repaymentStartDate.toString())
                                    bundle.putString("repaymentEndDate", repaymentEndDate.toString())
                                    bundle.putString("specialConditions", significant)
                                    bundle.putFloat("interestRate", rage.toFloat())
                                    if (paymentDateInt != null) {
                                        bundle.putInt("interestPaymentDate", paymentDateInt)
                                    }

                                    mainActivity.replaceFragment(MainActivity.IOU_WRITE_MY_FRAGMENT, true, bundle)
                                }
                            }

                            return
                        }
                    } catch (e: ParseException) {
                        Log.d(TAG, "rateValidateFields 의 e.printStackTrace() : ${e.printStackTrace()}")
                    }
                }

                if (rageText.isNotEmpty() && rageText[0] == '.') {
                    editTextRage.text?.clear()
                }

                buttonNext.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
            }

            if (!switchIouTransactionalInformation.isChecked) {

                // Set TextWatcher for editTextAmount
                editTextAmount.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        validateFields() // Call validateFields function when text changes
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

                editTextSignificant.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        validateFields()
                    }
                })

                editTextRage.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        validateFields()
                    }
                })

                editTextPaymentDate.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        validateFields()
                    }
                })

                // Call validateFields initially to set the button background
                validateFields()
            } else {

                editTextAmount.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        rateValidateFields()
                    }
                })

                editTextStart.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        rateValidateFields()
                    }
                })

                editTextDeadline.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        rateValidateFields()
                    }
                })

                editTextSignificant.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        rateValidateFields()
                    }
                })

                editTextRage.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        rateValidateFields()
                    }
                })

                editTextPaymentDate.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        rateValidateFields()
                    }
                })

                rateValidateFields()
            }

        }
    }

    private fun showAlertDialog() {
        val itemCancelBinding = ItemCancelBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemCancelBinding.root)
        val dialog = builder.create()

        // 작성 중단 - 네
        itemCancelBinding.textViewYesCancel.setOnClickListener {
            dialog.dismiss()

            mainActivity.removeAllBackStack()
        }

        // 작성 중단 - 아니오
        itemCancelBinding.textViewNoCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}

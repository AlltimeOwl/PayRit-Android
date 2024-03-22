package com.alltimeowl.payrit.ui.write

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.setFragmentResultListener
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentIouWriteOpponentBinding
import com.alltimeowl.payrit.databinding.ItemCancelBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject

class IouWriteOpponentFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouWriteOpponentBinding

    private var isButtonClickable: Boolean = false

    private lateinit var writerRole: String
    private var amount: Int? = null
    private var calcedAmount: Int? = null
    private lateinit var transactionDate: String
    private lateinit var repaymentStartDate: String
    private lateinit var repaymentEndDate: String
    private lateinit var specialConditions: String
    private var interestRate: Float? = null
    private var interestPaymentDate: Int? = null

    private var creditorName = ""
    private var creditorPhoneNumber = ""
    private var creditorAddress = ""

    private var debtorName = ""
    private var debtorPhoneNumber = ""
    private var debtorAddress = ""

    private var opponentName = ""
    private var opponentPhoneNumber = ""

    private var zonecode = ""
    private var roadAddress = ""
    private var detailAddress = ""
    private var opponentAddress = ""

    val TAG = "IouWriteOpponentFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouWriteOpponentBinding.inflate(layoutInflater)

        writerRole = arguments?.getString("writerRole").toString()
        amount = arguments?.getInt("amount")
        calcedAmount = arguments?.getInt("calcedAmount")
        transactionDate = arguments?.getString("transactionDate").toString()
        repaymentStartDate = arguments?.getString("repaymentStartDate").toString()
        repaymentEndDate = arguments?.getString("repaymentEndDate").toString()
        specialConditions = arguments?.getString("specialConditions").toString()
        interestRate = arguments?.getFloat("interestRate")
        interestPaymentDate = arguments?.let {
            if (it.containsKey("interestPaymentDate")) it.getInt("interestPaymentDate")
            else null
        }

        when(writerRole) {
            "CREDITOR" -> {
                creditorName = arguments?.getString("creditorName").toString()
                creditorPhoneNumber = arguments?.getString("creditorPhoneNumber").toString()
                creditorAddress = arguments?.getString("creditorAddress").toString()
            }

            "DEBTOR" -> {
                debtorName = arguments?.getString("debtorName").toString()
                debtorPhoneNumber = arguments?.getString("debtorPhoneNumber").toString()
                debtorAddress = arguments?.getString("debtorAddress").toString()
            }
        }

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            materialToolbarIouWriteOpponent.run {
                // 뒤로가기 버튼
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_WRITE_OPPONENT_FRAGMENT)
                }

                // X 버튼
                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.item_cancel -> showAlertDialog()
                    }
                    false
                }

            }

            // 이름 입력
            editTextNameIouWriteOpponent.addTextChangedListener(
                getTextWatcher(editTextNameIouWriteOpponent)
            )

            // 연락처 입력
            editTextPhoneNumberIouWriteOpponent.addTextChangedListener(
                getTextPhoneNumberWatcher(editTextPhoneNumberIouWriteOpponent)
            )

            // 우편번호 검색해서 받아온 값 (우편번호, 주소)
            setFragmentResultListener("addressDetailsInfo") { _, bundle ->
                val address = bundle.getString("address")

                // String -> Json 형태로 형변환
                val addressJson = JSONObject(address)

                zonecode = addressJson.getString("zonecode")
                roadAddress = addressJson.getString("roadAddress")
                val jibunAddress = addressJson.getString("jibunAddress")

                editTextZipCodeIouWriteOpponent.setText(zonecode)
                editTextAddressIouWriteOpponent.setText(roadAddress)

                updateAddress()
            }

            // 우편번호 검색
            buttonAddressSearchIouWriteOpponent.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.KAKAO_ZIP_CODE_FRAGMENT, true, null)
            }

            // 상세 주소 입력
            editTextDetailAddressIouWriteOpponent.addTextChangedListener(
                getTextAddressWatcher(editTextDetailAddressIouWriteOpponent)
            )

            // 다음 버튼
            buttonNextIouWriteOpponent.setOnClickListener {

                if (isButtonClickable) {

                    when(writerRole) {
                        "CREDITOR" -> {
                            val bundle = Bundle()

                            bundle.putString("writerRole", writerRole)
                            amount?.let { it1 -> bundle.putInt("amount", it1) }
                            calcedAmount?.let { it1 -> bundle.putInt("calcedAmount", it1) }
                            bundle.putString("transactionDate", transactionDate)
                            bundle.putString("repaymentStartDate", repaymentStartDate)
                            bundle.putString("repaymentEndDate", repaymentEndDate)
                            bundle.putString("specialConditions", specialConditions)
                            interestRate?.let { it1 -> bundle.putFloat("interestRate", it1)}
                            interestPaymentDate?.let { it1 -> bundle.putInt("interestPaymentDate", it1)}

                            bundle.putString("creditorName", creditorName)
                            bundle.putString("creditorPhoneNumber", creditorPhoneNumber)
                            bundle.putString("creditorAddress", creditorAddress)

                            bundle.putString("debtorName", opponentName)
                            bundle.putString("debtorPhoneNumber", opponentPhoneNumber)
                            bundle.putString("debtorAddress", opponentAddress)

                            mainActivity.replaceFragment(MainActivity.IOU_CONTENT_CHECK_FRAGMENT, true, bundle)
                        }

                        "DEBTOR" -> {
                            val bundle = Bundle()

                            bundle.putString("writerRole", writerRole)
                            amount?.let { it1 -> bundle.putInt("amount", it1) }
                            calcedAmount?.let { it1 -> bundle.putInt("calcedAmount", it1) }
                            bundle.putString("transactionDate", transactionDate)
                            bundle.putString("repaymentStartDate", repaymentStartDate)
                            bundle.putString("repaymentEndDate", repaymentEndDate)
                            bundle.putString("specialConditions", specialConditions)
                            interestRate?.let { it1 -> bundle.putFloat("interestRate", it1)}
                            interestPaymentDate?.let { it1 -> bundle.putInt("interestPaymentDate", it1)}

                            bundle.putString("creditorName", opponentName)
                            bundle.putString("creditorPhoneNumber", opponentPhoneNumber)
                            bundle.putString("creditorAddress", opponentAddress)

                            bundle.putString("debtorName", debtorName)
                            bundle.putString("debtorPhoneNumber", debtorPhoneNumber)
                            bundle.putString("debtorAddress", debtorAddress)

                            mainActivity.replaceFragment(MainActivity.IOU_CONTENT_CHECK_FRAGMENT, true, bundle)

                        }

                    }
                }
            }

        }
    }

    private fun getTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString()

                // 상대방 이름
                opponentName = text

                val phoneNumberText = binding.editTextPhoneNumberIouWriteOpponent.text.toString()

                // 전화번호 형식을 확인하는 정규 표현식
                val phoneNumberPattern = Regex("^010-\\d{4}-\\d{4}$")

                if (text.isNotEmpty() && phoneNumberPattern.matches(phoneNumberText)) {
                    // 텍스트가 비어있지 않고, 전화번호 형식이 올바른 경우
                    isButtonClickable = true
                    binding.buttonNextIouWriteOpponent.setBackgroundResource(R.drawable.bg_primary_mint_r12)
                } else {
                    // 텍스트가 비어있거나, 전화번호 형식이 올바르지 않은 경우
                    isButtonClickable = false
                    binding.buttonNextIouWriteOpponent.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
                }
            }

        }
    }

    private fun getTextPhoneNumberWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            private var isFormatting: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (isFormatting) {
                    return
                }

                isFormatting = true

                val text = editable.toString()

                // 상대방 잔화번호
                opponentPhoneNumber = text

                if (text.isEmpty()) {
                    isButtonClickable = false
                    binding.buttonNextIouWriteOpponent.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
                    isFormatting = false
                    return
                }

                val digits = text.filter { it.isDigit() }

                // 11자리를 초과하는 숫자는 잘라내기
                val trimmedDigits = if (digits.length > 11) digits.substring(0, 11) else digits

                // 숫자를 010-1234-5678 형식으로 변환
                val formattedNumber = buildString {
                    for (i in trimmedDigits.indices) {
                        append(trimmedDigits[i])
                        if (i == 2 || i == 6) {
                            // 마지막 위치가 아닐 때만 하이픈 추가
                            if (i < trimmedDigits.length - 1) append("-")
                        }
                    }
                }

                // 변경된 텍스트로 업데이트
                editable?.replace(0, editable.length, formattedNumber)

                // 전화번호 형식이 올바른지 검사하여 버튼의 배경을 변경합니다.
                if (binding.editTextNameIouWriteOpponent.text?.isNotEmpty() == true && formattedNumber.matches(Regex("^010-\\d{4}-\\d{4}$"))) {
                    // 올바른 형식의 전화번호일 때
                    isButtonClickable = true
                    binding.buttonNextIouWriteOpponent.setBackgroundResource(R.drawable.bg_primary_mint_r12)
                } else {
                    // 올바르지 않은 형식일 때
                    isButtonClickable = false
                    binding.buttonNextIouWriteOpponent.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
                }

                isFormatting = false
            }
        }
    }

    private fun getTextAddressWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                detailAddress = editable.toString()
                updateAddress()
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

    private fun updateAddress() {
        // 주소 정보가 비어 있는지 확인하고, 비어 있지 않을 경우만 문자열에 포함
        val formattedRoadAddress = if (roadAddress.isNotEmpty()) "$roadAddress " else ""
        val formattedDetailAddress = if (detailAddress.isNotEmpty()) "$detailAddress " else ""
        val formattedZonecode = if (zonecode.isNotEmpty()) "($zonecode)" else ""

        // 최종 주소 문자열을 구성
        opponentAddress = formattedRoadAddress + formattedDetailAddress + formattedZonecode
    }

}
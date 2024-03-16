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
import com.alltimeowl.payrit.databinding.FragmentIouWriteMyBinding
import com.alltimeowl.payrit.databinding.ItemCancelBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject

class IouWriteMyFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouWriteMyBinding

    private var isButtonClickable: Boolean = false

    val TAG = "IouWriteMyFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouWriteMyBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            materialToolbarIouTransactionalInformation.run {
                // 뒤로가기 버튼
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_WRITE_MY_FRAGMENT)
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
            editTextNameIouWriteMy.addTextChangedListener(
                getTextWatcher(editTextNameIouWriteMy)
            )

            // 연락처 입력
            editTextPhoneNumberIouWriteMy.addTextChangedListener(
                getTextPhoneNumberWatcher(editTextPhoneNumberIouWriteMy)
            )

            // 우편번호 검색해서 받아온 값 (우편번호, 주소)
            setFragmentResultListener("addressDetailsInfo") { _, bundle ->
                val address = bundle.getString("address")

                // String -> Json 형태로 형변환
                val addressJson = JSONObject(address)

                val zonecode = addressJson.getString("zonecode")
                val roadAddress = addressJson.getString("roadAddress")
                val jibunAddress = addressJson.getString("jibunAddress")

                editTextZipCodeIouWriteMy.setText(zonecode)
                editTextAddressIouWriteMy.setText(roadAddress)
            }

            // 우편번호 검색
            buttonAddressSearchIouWriteMy.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.KAKAO_ZIP_CODE_FRAGMENT, true, null)
            }

            // 다음 버튼 클릭
            buttonNextIouWriteMy.setOnClickListener {
                if (isButtonClickable) {
                    mainActivity.replaceFragment(MainActivity.IOU_WRITE_OPPONENT_FRAGMENT, true, null)
                }
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

    private fun getTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString()

                val phoneNumberText = binding.editTextPhoneNumberIouWriteMy.text.toString()

                // 전화번호 형식을 확인하는 정규 표현식
                val phoneNumberPattern = Regex("^010-\\d{4}-\\d{4}$")

                if (text.isNotEmpty() && phoneNumberPattern.matches(phoneNumberText)) {
                    // 텍스트가 비어있지 않고, 전화번호 형식이 올바른 경우
                    isButtonClickable = true
                    binding.buttonNextIouWriteMy.setBackgroundResource(R.drawable.bg_primary_mint_r12)
                } else {
                    // 텍스트가 비어있거나, 전화번호 형식이 올바르지 않은 경우
                    isButtonClickable = false
                    binding.buttonNextIouWriteMy.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
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

                if (text.isEmpty()) {
                    isButtonClickable = false
                    binding.buttonNextIouWriteMy.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
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
                if (binding.editTextNameIouWriteMy.text?.isNotEmpty() == true && formattedNumber.matches(Regex("^010-\\d{4}-\\d{4}$"))) {
                    // 올바른 형식의 전화번호일 때
                    isButtonClickable = true
                    binding.buttonNextIouWriteMy.setBackgroundResource(R.drawable.bg_primary_mint_r12)
                } else {
                    // 올바르지 않은 형식일 때
                    isButtonClickable = false
                    binding.buttonNextIouWriteMy.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
                }

                isFormatting = false
            }
        }
    }

}
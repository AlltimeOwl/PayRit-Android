package com.alltimeowl.payrit.ui.card

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentCardSettingBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class CardSettingFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentCardSettingBinding

    private var people = 0

    val TAG = "CardSettingFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentCardSettingBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            materialToolbarCardSetting.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CARD_SETTING_FRAGMENT)
                }
            }

            // "-" 버튼 클릭
            buttonMinusCardSetting.setOnClickListener {
                var currentValue = editTextCardSetting.text.toString().toInt()
                if (currentValue > 1) { // 최소값을 1로 설정
                    currentValue--
                    editTextCardSetting.setText(currentValue.toString())
                }
            }

            // "+" 버튼 클릭
            buttonPlusCardSetting.setOnClickListener {
                var currentValue = editTextCardSetting.text.toString().toInt()
                currentValue++
                editTextCardSetting.setText(currentValue.toString())
            }

            // editText 감지
            editTextCardSetting.addTextChangedListener(
                getTextWatcher(editTextCardSetting)
            )

            // editText 완료 시 포커스 제거
            editTextCardSetting.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // 키보드 숨기기
                    val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)

                    // EditText의 포커스 제거
                    v.clearFocus()

                    true
                } else {
                    false
                }
            }

            // 다음 버튼 클릭
            buttonNextCardSetting.setOnClickListener {
                val headcount = editTextCardSetting.text.toString().toInt()

                val bundle = Bundle()
                bundle.putInt("headcount", headcount)

                mainActivity.replaceFragment(MainActivity.CARD_SELECT_FRAGMENT, true, bundle)
            }

        }
    }

    private fun getTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val rawValue = s.toString().toIntOrNull() ?: 1 // null 또는 숫자가 아닌 입력 처리
                val value = if (rawValue < 1) 1 else rawValue // 입력된 값이 1보다 작을 경우 1로 설정

                updateMinusButtonImage(value)

                if (value != rawValue) { // 실제 입력된 값과 조정된 값이 다를 경우, EditText를 업데이트
                    editText.setText(value.toString())
                    editText.setSelection(editText.text.length) // 커서를 숫자 뒤로 이동
                } else {
                    // 입력된 숫자의 뒤로 커서를 이동
                    editText.setSelection(s.toString().length)
                }
            }
        }
    }

    // 버튼에 대한 조건에 따른 이미지 변경
    private fun updateMinusButtonImage(currentValue: Int) {
        if (currentValue > 1) {
            binding.buttonMinusCardSetting.setImageResource(R.drawable.ic_minus_box_on)
            binding.buttonNextCardSetting.setBackgroundResource(R.drawable.bg_primary_mint_r12)
            binding.buttonNextCardSetting.isEnabled = true
        } else {
            binding.buttonMinusCardSetting.setImageResource(R.drawable.ic_minus_box_off)
            binding.buttonNextCardSetting.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
            binding.buttonNextCardSetting.isEnabled = false
        }
    }

}
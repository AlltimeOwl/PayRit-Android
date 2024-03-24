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
import com.alltimeowl.payrit.ui.main.MainActivity.Companion.loginUserName
import com.alltimeowl.payrit.ui.main.MainActivity.Companion.loginUserPhoneNumber
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject

class IouWriteMyFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouWriteMyBinding

    private lateinit var writerRole: String
    private var amount: Int? = null
    private var interest: Int? = null
    private lateinit var transactionDate: String
    private lateinit var repaymentStartDate: String
    private lateinit var repaymentEndDate: String
    private lateinit var specialConditions: String
    private var interestRate: Float? = null
    private var interestPaymentDate: Int? = null

    private var zonecode = ""
    private var roadAddress = ""
    private var detailAddress = ""
    private var address = ""

    val TAG = "IouWriteMyFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouWriteMyBinding.inflate(layoutInflater)

        writerRole = arguments?.getString("writerRole").toString()
        amount = arguments?.getInt("amount")
        interest = arguments?.getInt("interest")
        transactionDate = arguments?.getString("transactionDate").toString()
        repaymentStartDate = arguments?.getString("repaymentStartDate").toString()
        repaymentEndDate = arguments?.getString("repaymentEndDate").toString()
        specialConditions = arguments?.getString("specialConditions").toString()
        interestRate = arguments?.getFloat("interestRate")
        interestPaymentDate = arguments?.let {
            if (it.containsKey("interestPaymentDate")) it.getInt("interestPaymentDate")
            else null
        }

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

            // 로그인한 유저 이름, 연락처 설정
            editTextNameIouWriteMy.setText(loginUserName)
            editTextPhoneNumberIouWriteMy.setText(mainActivity.convertPhoneNumber(loginUserPhoneNumber))

            // 우편번호 검색해서 받아온 값 (우편번호, 주소)
            setFragmentResultListener("addressDetailsInfo") { _, bundle ->
                val address = bundle.getString("address")

                // String -> Json 형태로 형변환
                val addressJson = JSONObject(address)

                zonecode = addressJson.getString("zonecode")
                roadAddress = addressJson.getString("roadAddress")
                val jibunAddress = addressJson.getString("jibunAddress")

                editTextZipCodeIouWriteMy.setText(zonecode)
                editTextAddressIouWriteMy.setText(roadAddress)

                updateAddress()
            }

            // 우편번호 검색
            buttonAddressSearchIouWriteMy.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.KAKAO_ZIP_CODE_FRAGMENT, true, null)
            }

            // 상세 주소 입력
            editTextDetailAddressIouWriteMy.addTextChangedListener(
                getTextWatcher(editTextDetailAddressIouWriteMy)
            )

            // 다음 버튼 클릭
            buttonNextIouWriteMy.setOnClickListener {

                when(writerRole) {
                    "CREDITOR" -> {

                        val bundle = Bundle()

                        bundle.putString("writerRole", writerRole)
                        amount?.let { it1 -> bundle.putInt("amount", it1) }
                        interest?.let { it1 -> bundle.putInt("interest", it1) }
                        bundle.putString("transactionDate", transactionDate)
                        bundle.putString("repaymentStartDate", repaymentStartDate)
                        bundle.putString("repaymentEndDate", repaymentEndDate)
                        bundle.putString("specialConditions", specialConditions)
                        interestRate?.let { it1 -> bundle.putFloat("interestRate", it1)}
                        interestPaymentDate?.let { it1 -> bundle.putInt("interestPaymentDate", it1)}
                        bundle.putString("creditorName", loginUserName)
                        bundle.putString("creditorPhoneNumber", loginUserPhoneNumber)
                        bundle.putString("creditorAddress", address)

                        mainActivity.replaceFragment(MainActivity.IOU_WRITE_OPPONENT_FRAGMENT, true, bundle)
                    }
                    "DEBTOR" -> {
                        val bundle = Bundle()

                        bundle.putString("writerRole", writerRole)
                        amount?.let { it1 -> bundle.putInt("amount", it1) }
                        interest?.let { it1 -> bundle.putInt("interest", it1) }
                        bundle.putString("transactionDate", transactionDate)
                        bundle.putString("repaymentStartDate", repaymentStartDate)
                        bundle.putString("repaymentEndDate", repaymentEndDate)
                        bundle.putString("specialConditions", specialConditions)
                        interestRate?.let { it1 -> bundle.putFloat("interestRate", it1)}
                        interestPaymentDate?.let { it1 -> bundle.putInt("interestPaymentDate", it1)}
                        bundle.putString("debtorName", loginUserName)
                        bundle.putString("debtorPhoneNumber", loginUserPhoneNumber)
                        bundle.putString("debtorAddress", address)

                        mainActivity.replaceFragment(MainActivity.IOU_WRITE_OPPONENT_FRAGMENT, true, bundle)
                    }
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
                detailAddress = editable.toString()
                updateAddress()
            }

        }
    }

    private fun updateAddress() {
        // 주소 정보가 비어 있는지 확인하고, 비어 있지 않을 경우만 문자열에 포함
        val formattedRoadAddress = if (roadAddress.isNotEmpty()) "$roadAddress " else ""
        val formattedDetailAddress = if (detailAddress.isNotEmpty()) "$detailAddress " else ""
        val formattedZonecode = if (zonecode.isNotEmpty()) "($zonecode)" else ""

        // 최종 주소 문자열을 구성
        address = formattedRoadAddress + formattedDetailAddress + formattedZonecode
    }

}
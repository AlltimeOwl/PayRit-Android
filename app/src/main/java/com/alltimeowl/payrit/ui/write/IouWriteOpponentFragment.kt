package com.alltimeowl.payrit.ui.write

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    val TAG = "IouWriteOpponentFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouWriteOpponentBinding.inflate(layoutInflater)

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

            // 우편번호 검색해서 받아온 값 (우편번호, 주소)
            setFragmentResultListener("addressDetailsInfo") { _, bundle ->
                val address = bundle.getString("address")

                // String -> Json 형태로 형변환
                val addressJson = JSONObject(address)

                val zonecode = addressJson.getString("zonecode")
                val roadAddress = addressJson.getString("roadAddress")
                val jibunAddress = addressJson.getString("jibunAddress")

                editTextZipCodeIouWriteOpponent.setText(zonecode)
                editTextAddressIouWriteOpponent.setText(roadAddress)
            }

            // 우편번호 검색
            buttonAddressSearchIouWriteOpponent.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.KAKAO_ZIP_CODE_FRAGMENT, true, null)
            }

            // 다음 버튼
            buttonNextIouWriteOpponent.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.IOU_CONTENT_CHECK_FRAGMENT, true, null)
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
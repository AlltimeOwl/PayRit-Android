package com.alltimeowl.payrit.ui.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentIouContentCheckBinding
import com.alltimeowl.payrit.databinding.ItemCancelBinding
import com.alltimeowl.payrit.databinding.ItemKakaolBinding
import com.alltimeowl.payrit.databinding.ItemKakaolCompleteBinding
import com.alltimeowl.payrit.databinding.ItemMmsBinding
import com.alltimeowl.payrit.databinding.ItemMmsCompleteBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class IouContentCheckFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouContentCheckBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouContentCheckBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            materialToolbarIouContentCheck.run {
                // 뒤로가기 버튼
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_CONTENT_CHECK_FRAGMENT)
                }

                // X 버튼
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.item_cancel -> showAlertDialog()
                    }
                    false
                }

                // 요청 전송하기
                buttonSendIouContentCheck.setOnClickListener {
                    showMmsAlertDialog()
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

    private fun showKakaoAlertDialog() {
        val itemKakaolBinding = ItemKakaolBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemKakaolBinding.root)
        val dialog = builder.create()

        // 카카오톡 요청 전송 - 아니오
        itemKakaolBinding.textViewNoKakao.setOnClickListener {
            dialog.dismiss()
        }

        // 카카오톡 요청 전송 - 네
        itemKakaolBinding.textViewYesKakao.setOnClickListener {
            dialog.dismiss()
            showKakaoCompleteAlertDialog()
        }

        dialog.show()
    }

    private fun showKakaoCompleteAlertDialog() {
        val itemKakaolCompleteBinding = ItemKakaolCompleteBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemKakaolCompleteBinding.root)
        val dialog = builder.create()

        // 카카오톡 요청완료 - 확인
        itemKakaolCompleteBinding.textViewCompleteKakao.setOnClickListener {
            dialog.dismiss()

            mainActivity.removeAllBackStack()
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showMmsAlertDialog() {
        val itemMmsBinding = ItemMmsBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemMmsBinding.root)
        val dialog = builder.create()

        // MMS 요청 전송 - 아니오
        itemMmsBinding.textViewNoMms.setOnClickListener {
            dialog.dismiss()
        }

        // MMS 요청 전송 - 네
        itemMmsBinding.textViewYesMms.setOnClickListener {
            dialog.dismiss()
            showMmsCompleteAlertDialog()
        }

        dialog.show()
    }

    private fun showMmsCompleteAlertDialog() {
        val itemMmsCompleteBinding = ItemMmsCompleteBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemMmsCompleteBinding.root)
        val dialog = builder.create()

        // MMS 요청완료 - 확인
        itemMmsCompleteBinding.textViewCompleteMms.setOnClickListener {
            dialog.dismiss()

            mainActivity.removeAllBackStack()
            mainActivity.selectBottomNavigationItem(R.id.home_menu)
        }

        dialog.setCancelable(false)
        dialog.show()
    }

}
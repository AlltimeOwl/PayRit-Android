package com.alltimeowl.payrit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.FragmentIouDetailBinding
import com.alltimeowl.payrit.databinding.ItemCancelBinding
import com.alltimeowl.payrit.databinding.ItemDocumentBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class IouDetailFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentIouDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentIouDetailBinding.inflate(layoutInflater)

        initUI()

        return binding.root
    }

    private fun initUI() {
        binding.run {
            mainActivity.hideBottomNavigationView()

            materialToolbarIouDetail.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IOU_DETAIL_FRAGMENT)
                }
            }

            // 일부 상환 기록 클릭
            buttonRecordIouDetail.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.IOU_DETAIL_AMOUNT_RECEIVED_FRAGMENT, true, null)
            }

            // PDF·메일 내보내기 클릭
            buttonDocumentIouDetail.setOnClickListener {
                showBottomSheet()
            }

            // 개인 메모 클릭
            imageViewMemoIouDetail.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.IOU_DETAIL_MEMO_FRAGMENT, true, null)
            }

        }
    }

    private fun showBottomSheet() {
        val bottomSheetView = ItemDocumentBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(mainActivity)

        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.background = null

            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            // PDF 다운
            bottomSheetView.linearLayoutDownloadPdfItemDocument.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            // 이메일 전송
            bottomSheetView.linearLayoutSendEmailItemDocument.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            // 취소
            bottomSheetView.linearLayoutCancelItemDocument.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

        }

        bottomSheetDialog.setContentView(bottomSheetView.root)
        bottomSheetDialog.show()
    }

}
package com.alltimeowl.payrit.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.DialogBottomNotificationPaymentHistoryBinding
import com.alltimeowl.payrit.databinding.ItemPaymentHistoryBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class PaymentHistoryAdapter(val mainActivity: MainActivity): RecyclerView.Adapter<PaymentHistoryAdapter.PaymentHistoryViewHolder>() {

    inner class PaymentHistoryViewHolder(private val binding: ItemPaymentHistoryBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            // 편집 아이콘 클릭
            binding.imageViewEditPaymentHistory.setOnClickListener {

                val bottomSheetView = DialogBottomNotificationPaymentHistoryBinding.inflate(LayoutInflater.from(mainActivity), null, false)
                val bottomSheetDialog = BottomSheetDialog(mainActivity)

                // BottomSheetDialog의 Window에 대한 속성 설정
                bottomSheetDialog.setOnShowListener {
                    val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                    bottomSheet?.background = null
                }

                bottomSheetDialog.setContentView(bottomSheetView.root)
                bottomSheetDialog.show()

                // 내역 상세 보기
                bottomSheetView.linearLayoutShowDetailPaymentHistory.setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.PAYMENT_HISTORY_DETAIL_FRAGMENT, true, null)
                    bottomSheetDialog.dismiss()
                }

                // 취소
                bottomSheetView.linearLayoutCancelPaymentHistory.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHistoryViewHolder {
        val binding = ItemPaymentHistoryBinding.inflate(LayoutInflater.from(parent.context))
        val paymentHistoryViewHolder = PaymentHistoryViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return paymentHistoryViewHolder
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: PaymentHistoryViewHolder, position: Int) {
        holder.bind()
    }
}
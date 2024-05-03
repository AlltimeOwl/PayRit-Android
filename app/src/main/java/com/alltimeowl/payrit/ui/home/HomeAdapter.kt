package com.alltimeowl.payrit.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.getMyIouListResponse
import com.alltimeowl.payrit.databinding.ItemApprovalRequestBinding
import com.alltimeowl.payrit.databinding.ItemIouBinding
import com.alltimeowl.payrit.databinding.ItemModifyingBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.Math.abs

class HomeAdapter(val mainActivity: MainActivity, var myIouList: MutableList<getMyIouListResponse>): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(private val binding: ItemIouBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val iou = myIouList[position]

                    Log.d("HomeFragment", "클릭된 iou : ${iou}")

                    if (iou.paperRole == "CREDITOR" && (iou.paperStatus == "COMPLETE_WRITING" || iou.paperStatus == "EXPIRED")) {

                        val bundle = Bundle()
                        bundle.putInt("paperId", iou.paperId)

                        mainActivity.replaceFragment(MainActivity.IOU_DETAIL_FRAGMENT, true, bundle)
                    } else if (iou.paperRole == "DEBTOR" && (iou.paperStatus == "COMPLETE_WRITING" || iou.paperStatus == "EXPIRED")) {
                        // 채무자 가 보는 페이릿 상세화면

                        val bundle = Bundle()
                        bundle.putInt("paperId", iou.paperId)

                        mainActivity.replaceFragment(MainActivity.IOU_BORROW_DETAIL_FRAGMENT, true, bundle)
                    } else if (iou.isWriter && iou.paperStatus == "WAITING_AGREE") {
                        // 차용증 작성자가 승인 대기중 클릭시

                        val bundle = Bundle()
                        bundle.putInt("paperId", iou.paperId)
                        bundle.putString("paperStatus", iou.paperStatus)

                        mainActivity.replaceFragment(MainActivity.IOU_WRITER_APPROVAL_WAITING_FRAGMENT, true, bundle)

                    } else if (!iou.isWriter && iou.paperStatus == "WAITING_AGREE") {  // 다른 사람이 보낸 차용증 승인 요청

                        val bundle = Bundle()
                        bundle.putInt("paperId", iou.paperId)

                        mainActivity.replaceFragment(MainActivity.RECIPIENT_APPROVAL_FRAGMENT, true, bundle)
                    } else if (iou.isWriter && iou.paperStatus == "PAYMENT_REQUIRED") {  // 차용증 결제 요청

                        val bundle = Bundle()
                        bundle.putInt("paperId", iou.paperId)

                        mainActivity.replaceFragment(MainActivity.PAYMENT_FRAGMENT, true, bundle)
                    } else if (iou.isWriter && iou.paperStatus == "MODIFYING") {
                        // 차용증 작성자가 수정 요청중 클릭시

                        val bundle = Bundle()
                        bundle.putInt("paperId", iou.paperId)
                        bundle.putString("paperStatus", iou.paperStatus)

                        Log.d("HomeFragment", "bundle : ${bundle}")

                        mainActivity.replaceFragment(MainActivity.IOU_WRITER_APPROVAL_WAITING_FRAGMENT, true, bundle)
                    } else if (!iou.isWriter && iou.paperStatus == "MODIFYING") {
                        // 차용증 받은 사람이 수정 요청중 클릭시

                        val itemModifyingBinding = ItemModifyingBinding.inflate(mainActivity.layoutInflater)
                        val builder = MaterialAlertDialogBuilder(mainActivity)
                        builder.setView(itemModifyingBinding.root)
                        val dialog = builder.create()

                        itemModifyingBinding.textViewCheckModifiying.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show()
                    }
                }
            }
        }

        fun bind(iou: getMyIouListResponse) {

            binding.textViewRepaymentEndDateIou.text = "원금상환일 ${mainActivity.convertDateFormat(iou.repaymentEndDate)}"
            binding.textViewAmountIou.text = "${mainActivity.convertMoneyFormat(iou.amount)}원"
            binding.textViewPeerNameIou.text = iou.peerName
            binding.progressBarIou.progress = iou.repaymentRate.toInt()
            binding.textViewRepaymentRateIou.text = "(${iou.repaymentRate.toInt()}%)"

            when(iou.paperRole) {
                "CREDITOR" -> {
                    binding.constraintLayoutIou.setBackgroundResource(R.drawable.bg_primary_mint_r8)
                    binding.textViewPaperRoleIou.text = "빌려준 돈"
                    binding.textViewPaperRoleIou.setTextColor(ContextCompat.getColor(mainActivity, R.color.primaryMint))
                    binding.progressBarIou.progressDrawable = ContextCompat.getDrawable(mainActivity, R.drawable.bg_progress_bar_mint)
                }

                "DEBTOR" -> {
                    binding.constraintLayoutIou.setBackgroundResource(R.drawable.bg_pink_r8)
                    binding.textViewPaperRoleIou.text = "빌린 돈"
                    binding.textViewPaperRoleIou.setTextColor(ContextCompat.getColor(mainActivity, R.color.pink))
                    binding.progressBarIou.progressDrawable = ContextCompat.getDrawable(mainActivity, R.drawable.bg_progress_bar_pink)
                }
            }

            if (iou.dueDate >= 0) {
                binding.textViewDueDateIou.text = "D - ${iou.dueDate}"
            } else {
                binding.textViewDueDateIou.text = "D + ${abs(iou.dueDate)}"
            }

            when(iou.paperStatus) {
                "WAITING_AGREE" -> binding.textViewPaperStatusIou.text = "승인 대기중"
                "MODIFYING" -> binding.textViewPaperStatusIou.text = "수정 요청중"
                "PAYMENT_REQUIRED" -> binding.textViewPaperStatusIou.text = "결제 대기중"
                "COMPLETE_WRITING" -> binding.textViewPaperStatusIou.text = "상환 진행중"
                "EXPIRED" -> binding.textViewPaperStatusIou.text = "상환 완료"
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {

        val binding = ItemIouBinding.inflate(LayoutInflater.from(parent.context))
        val homeViewHolder = HomeViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return homeViewHolder

    }

    override fun getItemCount(): Int {
        return myIouList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(myIouList[position])
    }

    fun updateData(newData: List<getMyIouListResponse>) {
        myIouList = newData.toMutableList()
        notifyDataSetChanged()
    }

}
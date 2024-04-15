package com.alltimeowl.payrit.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.data.model.DeleteRepaymentRequest
import com.alltimeowl.payrit.data.model.RepaymentHistory
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.ItemReceivedBinding
import com.alltimeowl.payrit.databinding.ItemRepaymentDeleteBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class IouDetailAmountReceivedAdapter(
    val mainActivity: MainActivity, private val homeViewModel: HomeViewModel,
    val paperId: Int,
    var repaymentList: MutableList<RepaymentHistory>
) : RecyclerView.Adapter<IouDetailAmountReceivedAdapter.IouDetailAmountReceivedViewHolder>() {

    inner class IouDetailAmountReceivedViewHolder(private val binding: ItemReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imageViewDeleteItemReceived.setOnClickListener {
                val position = adapterPosition
                val repayment = repaymentList[position]
                val deleteRepaymentRequest = DeleteRepaymentRequest(paperId, repayment.id)
                showAlertDialog(deleteRepaymentRequest)
            }

        }

        fun bind(repay: RepaymentHistory) {
            binding.textViewDateItemReceived.text = mainActivity.iouConvertDateFormat(repay.repaymentDate)
            binding.textViewAmountItemReceived.text = mainActivity.convertMoneyFormat(repay.repaymentAmount) + "원"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IouDetailAmountReceivedViewHolder {

        val binding = ItemReceivedBinding.inflate(LayoutInflater.from(parent.context))
        val iouDetailAmountReceivedViewHolder = IouDetailAmountReceivedViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return iouDetailAmountReceivedViewHolder
    }

    override fun getItemCount(): Int {
        return repaymentList.size
    }

    override fun onBindViewHolder(holder: IouDetailAmountReceivedViewHolder, position: Int) {
        holder.bind(repaymentList[position])
    }

    fun updateData(newData: List<RepaymentHistory>) {
        repaymentList = newData.toMutableList()
        notifyDataSetChanged()
    }

    fun showAlertDialog(deleteRepaymentRequest: DeleteRepaymentRequest) {
        val itemRepaymentDeleteBinding =
            ItemRepaymentDeleteBinding.inflate(LayoutInflater.from(mainActivity))
        val builder = MaterialAlertDialogBuilder(mainActivity)
        builder.setView(itemRepaymentDeleteBinding.root)
        val dialog = builder.create()

        // 삭제 - 네
        itemRepaymentDeleteBinding.textViewYesRepaymentDelete.setOnClickListener {
            val accessToken = SharedPreferencesManager.getAccessToken()
            homeViewModel.deleteRepayment(accessToken, deleteRepaymentRequest, paperId)
            val indexToRemove = repaymentList.indexOfFirst { it.id ==  deleteRepaymentRequest.historyId }
            if (indexToRemove != -1) {
                repaymentList.removeAt(indexToRemove)
                notifyItemRemoved(indexToRemove)
                notifyDataSetChanged()
            }

            dialog.dismiss()
        }

        // 삭제 - 아니오
        itemRepaymentDeleteBinding.textViewNoRepaymentDelete.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
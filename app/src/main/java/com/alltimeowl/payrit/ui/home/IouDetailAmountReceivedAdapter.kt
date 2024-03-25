package com.alltimeowl.payrit.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.data.model.RepaymentHistory
import com.alltimeowl.payrit.databinding.ItemReceivedBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class IouDetailAmountReceivedAdapter(val mainActivity: MainActivity, var repaymentList: MutableList<RepaymentHistory>) : RecyclerView.Adapter<IouDetailAmountReceivedAdapter.IouDetailAmountReceivedViewHolder>() {

    inner class IouDetailAmountReceivedViewHolder(private val binding: ItemReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {

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

}
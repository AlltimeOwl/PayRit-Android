package com.alltimeowl.payrit.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.data.sampleMemo
import com.alltimeowl.payrit.databinding.ItemMemoBinding

class IouDetailMemoAdapter(val sampleMemoList: MutableList<sampleMemo>) : RecyclerView.Adapter<IouDetailMemoAdapter.IouDetailMemoViewHolder>() {

    private val uniqueDates = mutableSetOf<String>()

    inner class IouDetailMemoViewHolder(private val binding: ItemMemoBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(memo: sampleMemo) {
                binding.textViewDateItemMemo.text = memo.date

                // Check if the date is unique, if yes, add it to the set
                if (uniqueDates.add(memo.date)) {
                    binding.textViewDateItemMemo.visibility = View.VISIBLE
                } else {
                    // If the date is not unique, hide the date TextView
                    binding.textViewDateItemMemo.visibility = View.GONE
                }

                binding.textViewContentsItemMemo.text = memo.contents
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IouDetailMemoViewHolder {

        val binding = ItemMemoBinding.inflate(LayoutInflater.from(parent.context))
        val iouDetailMemoViewHolder = IouDetailMemoViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return iouDetailMemoViewHolder
    }

    override fun getItemCount(): Int {
        return sampleMemoList.size
    }

    override fun onBindViewHolder(holder: IouDetailMemoViewHolder, position: Int) {
        holder.bind(sampleMemoList[position])
    }

}
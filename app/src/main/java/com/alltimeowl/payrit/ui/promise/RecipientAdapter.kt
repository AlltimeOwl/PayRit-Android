package com.alltimeowl.payrit.ui.promise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.data.model.PromiseData
import com.alltimeowl.payrit.databinding.ItemRecipientBinding

class RecipientAdapter(private val promiseDataList: ArrayList<PromiseData>): RecyclerView.Adapter<RecipientAdapter.RecipientViewHolder>() {

    inner class RecipientViewHolder(val binding: ItemRecipientBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(promiseData : PromiseData) {
            binding.textViewRecipientInformationItemRecipient.text = "${promiseData.name}  |  ${promiseData.phoneNumber}"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipientViewHolder {

        val binding = ItemRecipientBinding.inflate(LayoutInflater.from(parent.context))
        val recipientViewHolder = RecipientViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return recipientViewHolder
    }

    override fun getItemCount(): Int {
        return promiseDataList.size
    }

    override fun onBindViewHolder(holder: RecipientViewHolder, position: Int) {
        holder.bind(promiseDataList[position])
    }

}
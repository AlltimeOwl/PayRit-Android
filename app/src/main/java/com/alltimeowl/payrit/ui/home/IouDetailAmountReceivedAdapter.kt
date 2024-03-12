package com.alltimeowl.payrit.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.databinding.ItemReceivedBinding

class IouDetailAmountReceivedAdapter : RecyclerView.Adapter<IouDetailAmountReceivedAdapter.IouDetailAmountReceivedViewHolder>() {

    inner class IouDetailAmountReceivedViewHolder(private val binding: ItemReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
        return 3
    }

    override fun onBindViewHolder(holder: IouDetailAmountReceivedViewHolder, position: Int) {

    }

}
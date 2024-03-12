package com.alltimeowl.payrit.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.databinding.ItemReceivedBinding

class IouDetailAdapter : RecyclerView.Adapter<IouDetailAdapter.IouDetailViewHolder>() {

    inner class IouDetailViewHolder(private val binding: ItemReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IouDetailViewHolder {

        val binding = ItemReceivedBinding.inflate(LayoutInflater.from(parent.context))
        val iouDetailViewHolder = IouDetailViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return iouDetailViewHolder
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: IouDetailViewHolder, position: Int) {

    }

}
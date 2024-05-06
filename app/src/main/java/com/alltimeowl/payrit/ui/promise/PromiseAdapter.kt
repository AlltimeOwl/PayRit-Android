package com.alltimeowl.payrit.ui.promise

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.data.model.PromiseData
import com.alltimeowl.payrit.databinding.ItemPromiseBinding

class PromiseAdapter(
    private val fragment: PromiseContactFragment,
    private var promiseList: MutableList<PromiseData>,
    private val viewModel: PromiseViewModel
) : RecyclerView.Adapter<PromiseAdapter.PromiseViewHolder>() {

    inner class PromiseViewHolder(private val binding: ItemPromiseBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(promiseItem: PromiseData) {

            binding.textViewRecipientNameItemPromise.text = promiseItem.name
            binding.editTextRecipientPhoneNumberItemPromise.text = promiseItem.phoneNumber

            binding.imageViewClearItemPromise.setOnClickListener {
                removeItem(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromiseViewHolder {

        val binding = ItemPromiseBinding.inflate(LayoutInflater.from(parent.context))
        val promiseViewHolder = PromiseViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return promiseViewHolder
    }

    override fun getItemCount(): Int {
        return promiseList.size
    }

    override fun onBindViewHolder(holder: PromiseViewHolder, position: Int) {
        holder.bind(promiseList[position])
    }

    fun getListSize(): Int {
        return promiseList.size
    }

    fun addItem(promiseItem: PromiseData) {
        viewModel.addPromiseData(promiseItem)
        updateData(promiseList)
    }

    fun removeItem(position: Int) {
        viewModel.removePromiseData(position)
        updateData(promiseList)
    }

    fun updateData(newPromiseList: MutableList<PromiseData>) {
        promiseList = newPromiseList
        notifyDataSetChanged()
        fragment.checkButton()
    }

}
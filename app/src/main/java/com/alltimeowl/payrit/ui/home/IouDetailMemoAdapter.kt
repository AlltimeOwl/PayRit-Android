package com.alltimeowl.payrit.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.data.model.MemoListResponse
import com.alltimeowl.payrit.databinding.ItemMemoBinding

class IouDetailMemoAdapter(var memoList: MutableList<MemoListResponse>) : RecyclerView.Adapter<IouDetailMemoAdapter.IouDetailMemoViewHolder>() {

    inner class IouDetailMemoViewHolder(private val binding: ItemMemoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(memo: MemoListResponse, showDate: Boolean) {
            val regex = Regex("\\d{4}-\\d{2}-\\d{2}") // 날짜 형식에 맞는 정규 표현식
            val datePart = regex.find(memo.createdAt)?.value // createdAt에서 날짜 형식에 맞는 부분을 찾습니다.

            val formattedDate = datePart?.replace("-", ".") ?: memo.createdAt

            if (showDate) {
                binding.textViewDateItemMemo.visibility = ViewGroup.VISIBLE
                binding.textViewDateItemMemo.text = formattedDate
            } else {
                binding.textViewDateItemMemo.visibility = ViewGroup.GONE
            }

            binding.textViewContentsItemMemo.text = memo.content
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
        return memoList.size
    }

    override fun onBindViewHolder(holder: IouDetailMemoViewHolder, position: Int) {
        val currentMemo = memoList[position]
        // 이전 항목과의 날짜를 비교합니다. position이 0이면 이전 항목이 없으므로 항상 날짜를 표시합니다.
        val showDate = if (position > 0) {
            val prevMemo = memoList[position - 1]
            // 날짜가 다른 경우에만 true를 반환합니다.
            !currentMemo.createdAt.startsWith(prevMemo.createdAt.substring(0, 10))
        } else true

        holder.bind(currentMemo, showDate)
    }

    fun updateData(newData: List<MemoListResponse>) {
        memoList = newData.toMutableList()
        notifyDataSetChanged()
    }

}
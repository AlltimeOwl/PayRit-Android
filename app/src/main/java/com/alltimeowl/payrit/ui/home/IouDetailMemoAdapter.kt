package com.alltimeowl.payrit.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alltimeowl.payrit.data.model.MemoListResponse
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.ItemMemoBinding
import com.alltimeowl.payrit.databinding.ItemMemoDeleteBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class IouDetailMemoAdapter(val mainActivity: MainActivity, private val homeViewModel: HomeViewModel, var memoList: MutableList<MemoListResponse>) : RecyclerView.Adapter<IouDetailMemoAdapter.IouDetailMemoViewHolder>() {

    inner class IouDetailMemoViewHolder(private val binding: ItemMemoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imageViewDeleteItemMemo.setOnClickListener {
                val position = adapterPosition
                val memo = memoList[position]
                showAlertDialog(mainActivity, memo)
            }
        }

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

    fun showAlertDialog(context: Context, memo:  MemoListResponse) {
        val itemMemoDeleteBinding = ItemMemoDeleteBinding.inflate(LayoutInflater.from(context))
        val builder = MaterialAlertDialogBuilder(context)
        builder.setView(itemMemoDeleteBinding.root)
        val dialog = builder.create()

        // 메모 삭제 - 네
        itemMemoDeleteBinding.textViewYesMemoDelete.setOnClickListener {
            val accessToken = SharedPreferencesManager.getAccessToken()
            homeViewModel.deleteMemo(accessToken, memo.id)
            val indexToRemove = memoList.indexOfFirst { it.id == memo.id }
            if (indexToRemove != -1) {
                memoList.removeAt(indexToRemove)
                notifyItemRemoved(indexToRemove)
                notifyDataSetChanged()
            }

            dialog.dismiss()
        }

        // 메모 삭제 - 아니오
        itemMemoDeleteBinding.textViewNoMemoDelete.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

}
package com.alltimeowl.payrit.ui.mypage

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.ItemWithdrawalSpinnerBinding

class WithdrawalSpinnerAdapter(context: Context, resource: Int, items: Array<String>): ArrayAdapter<String>(context, resource, items) {

    var isDropdownOpen: Boolean = false

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        if (position == 0) {
            val binding: ItemWithdrawalSpinnerBinding
            val view: View

            if (convertView == null) {
                binding = ItemWithdrawalSpinnerBinding.inflate(LayoutInflater.from(context), parent, false)
                view = binding.root
                view.tag = binding
            } else {
                view = convertView
                binding = convertView.tag as ItemWithdrawalSpinnerBinding
            }

            binding.textViewWithdrawalSpinner.text = getItem(position)
            binding.textViewWithdrawalSpinner.setTextColor(Color.parseColor("#999999"))

            return view
        } else {
            return getCustomView(position, convertView, parent)
        }

    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemWithdrawalSpinnerBinding
        val view: View

        if (convertView == null) {
            binding = ItemWithdrawalSpinnerBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = convertView.tag as ItemWithdrawalSpinnerBinding
        }

        binding.textViewWithdrawalSpinner.text = getItem(position)

        if (isDropdownOpen && position == 0) {
            val drawable = context.resources.getDrawable(R.drawable.ic_up, null)
            binding.textViewWithdrawalSpinner.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            binding.textViewWithdrawalSpinner.setTextColor(Color.parseColor("#999999"))
        } else {
            binding.textViewWithdrawalSpinner.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

        return view
    }

}
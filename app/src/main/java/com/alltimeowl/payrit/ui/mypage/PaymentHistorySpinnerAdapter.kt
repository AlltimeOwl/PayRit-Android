package com.alltimeowl.payrit.ui.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.ItemPaymentHistorySpinnerBinding

class PaymentHistorySpinnerAdapter(context: Context, resource: Int, items: Array<String>): ArrayAdapter<String>(context, resource, items) {

    var isDropdownOpen: Boolean = false

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val binding: ItemPaymentHistorySpinnerBinding
        val view: View

        if (convertView == null) {
            binding = ItemPaymentHistorySpinnerBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = convertView.tag as ItemPaymentHistorySpinnerBinding
        }

        binding.textViewPaymentHistorySpinner.text = getItem(position)

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemPaymentHistorySpinnerBinding
        val view: View

        if (convertView == null) {
            binding = ItemPaymentHistorySpinnerBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = convertView.tag as ItemPaymentHistorySpinnerBinding
        }

        binding.textViewPaymentHistorySpinner.text = getItem(position)

        if (isDropdownOpen && position == 0) {
            val drawable = context.resources.getDrawable(R.drawable.ic_withdrawal_spinner, null)
            binding.textViewPaymentHistorySpinner.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else {
            binding.textViewPaymentHistorySpinner.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

        return view
    }

}
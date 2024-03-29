package com.alltimeowl.payrit.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.databinding.ItemSpinnerBinding

class SpinnerAdapter(context: Context, resource: Int, private val items: Array<String>): ArrayAdapter<String>(context, resource, items) {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var isDropdownOpen: Boolean = false

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val binding: ItemSpinnerBinding
        val view: View

        if (convertView == null) {
            binding = ItemSpinnerBinding.inflate(inflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemSpinnerBinding
        }

        binding.textViewSpinnerItemCategory.text = getItem(position)

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {

        val binding: ItemSpinnerBinding
        val view: View

        if (convertView == null) {
            binding = ItemSpinnerBinding.inflate(inflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemSpinnerBinding
        }

        val text = binding.textViewSpinnerItemCategory

        val item = items[position]
        text.text = item

        if (isDropdownOpen && position == 0) {
            val drawable = context.resources.getDrawable(R.drawable.ic_down, null)
            binding.textViewSpinnerItemCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else {
            binding.textViewSpinnerItemCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

        return view
    }

}
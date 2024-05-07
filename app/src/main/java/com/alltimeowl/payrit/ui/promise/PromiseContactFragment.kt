package com.alltimeowl.payrit.ui.promise

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alltimeowl.payrit.R
import com.alltimeowl.payrit.data.model.PromiseData
import com.alltimeowl.payrit.data.model.SharedPreferencesManager
import com.alltimeowl.payrit.databinding.FragmentPromiseContactBinding
import com.alltimeowl.payrit.ui.main.MainActivity

class PromiseContactFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentPromiseContactBinding

    private lateinit var viewModel: PromiseViewModel

    private lateinit var recipientInfoList: MutableList<PromiseData>

    companion object {
        private const val REQUEST_SELECT_CONTACT = 1
    }

    private var isButtonClickable: Boolean = false

    val TAG = "PromiseContactFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentPromiseContactBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[PromiseViewModel::class.java]

        initUI()
        observeData()

        return binding.root
    }

    private fun initUI() {
        binding.run {

            materialToolbarPromiseContact.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PROMISE_CONTACT_FRAGMENT)
                }

                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.item_cancel -> mainActivity.showCancelAlertDialog()
                    }
                    false
                }
            }

            editTextSenderNamePromiseContact.setText(SharedPreferencesManager.getUserName())

            recyclerViewPromiseContact.run {
                recyclerViewPromiseContact.layoutManager = LinearLayoutManager(context)
                adapter = PromiseAdapter(this@PromiseContactFragment, mutableListOf(), viewModel)
            }

            // 연락처 입력
            editTextRecipientPhoneNumberPromiseContact.addTextChangedListener(
                getTextPhoneNumberWatcher(editTextRecipientPhoneNumberPromiseContact)
            )

            // + 기호 클릭시
            imageViewPlusPromiseContact.setOnClickListener {
                plusPromise()
            }

            // 다음 버튼 클릭시
            buttonNextPromiseContact.setOnClickListener {
                if (isButtonClickable) {

                    val bundle = Bundle()
                    bundle.putParcelableArrayList("promiseDataList", ArrayList(recipientInfoList))

                    mainActivity.replaceFragment(MainActivity.PROMISE_INFORMATION_FRAGMENT, true, bundle)
                }
            }

        }
    }

    private fun getTextPhoneNumberWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val text = editable.toString()

                val digits = text.filter { it.isDigit() }

                // 11자리를 초과하는 숫자는 잘라내기
                val trimmedDigits = if (digits.length > 11) digits.substring(0, 11) else digits

                // 숫자를 010-1234-5678 형식으로 변환
                val formattedNumber = buildString {
                    for (i in trimmedDigits.indices) {
                        append(trimmedDigits[i])
                        if (i == 2 || i == 6) {
                            // 마지막 위치가 아닐 때만 하이픈 추가
                            if (i < trimmedDigits.length - 1) append("-")
                        }
                    }
                }

                if (editable.toString() != formattedNumber) {
                    editText.removeTextChangedListener(this)
                    editable?.replace(0, editable.length, formattedNumber)
                    editText.addTextChangedListener(this)
                }

            }

        }
    }

    private fun plusPromise() {

        val name = binding.editTextRecipientNamePromiseContact.text.toString()
        val phoneNumber = binding.editTextRecipientPhoneNumberPromiseContact.text.toString()

        if (name.isNotBlank() && phoneNumber.isNotBlank()) {
            val newContact = PromiseData(name, phoneNumber)

            (binding.recyclerViewPromiseContact.adapter as PromiseAdapter).addItem(newContact)

            // 입력 필드 초기화
            binding.editTextRecipientNamePromiseContact.text.clear()
            binding.editTextRecipientPhoneNumberPromiseContact.text.clear()

        } else if (name.isEmpty() || phoneNumber.isEmpty()) {

            if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_SELECT_CONTACT)
            } else {
                accessContacts()
            }

        }

    }

    private fun accessContacts() {
        val contactPickerIntent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(contactPickerIntent, REQUEST_SELECT_CONTACT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val projection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )
                val cursor = mainActivity.contentResolver.query(uri, projection, null, null, null)
                cursor?.use { cur ->
                    if (cur.moveToFirst()) {
                        val nameIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        val numberIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                        if (nameIndex != -1 && numberIndex != -1) {
                            val name = cur.getString(nameIndex)
                            val phoneNumber = cur.getString(numberIndex)

                            val newContact = PromiseData(name, phoneNumber)

                            (binding.recyclerViewPromiseContact.adapter as PromiseAdapter).addItem(newContact)
                        }
                    }
                }
            }
        }
    }

    fun checkButton() {
        binding.run {
            if ((binding.recyclerViewPromiseContact.adapter as PromiseAdapter).getListSize() > 0) {
                buttonNextPromiseContact.setBackgroundResource(R.drawable.bg_primary_mint_r12)
                isButtonClickable = true
            } else {
                buttonNextPromiseContact.setBackgroundResource(R.drawable.bg_gray_scale07_r12)
                isButtonClickable = false
            }
        }
    }

    private fun observeData() {
        viewModel.promiseList.observe(viewLifecycleOwner) { promiseList ->
            (binding.recyclerViewPromiseContact.adapter as PromiseAdapter).updateData(promiseList)
            recipientInfoList = promiseList
        }
    }

}

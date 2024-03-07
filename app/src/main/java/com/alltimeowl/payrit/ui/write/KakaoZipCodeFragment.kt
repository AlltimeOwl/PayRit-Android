package com.alltimeowl.payrit.ui.write

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.fragment.app.setFragmentResult
import com.alltimeowl.payrit.databinding.FragmentKakaoZipCodeBinding
import com.alltimeowl.payrit.ui.main.MainActivity
import org.json.JSONObject

class KakaoZipCodeFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    lateinit var binding: FragmentKakaoZipCodeBinding

    inner class BridgeInterface {
        @JavascriptInterface
        fun processDATA(data: String?) {

            val postData = JSONObject(data)

            val bundle = Bundle()
            bundle.putString("address", postData.toString())

            setFragmentResult("addressDetailsInfo", bundle)

            mainActivity.removeFragment(MainActivity.KAKAO_ZIP_CODE_FRAGMENT)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        binding = FragmentKakaoZipCodeBinding.inflate(layoutInflater)

        binding.run {
            webViewKakaoZipCode.run {
                settings.javaScriptEnabled = true
                addJavascriptInterface(BridgeInterface(), "Android")

                loadUrl("https://wjdwntjd55.github.io/Kakao_Adress_Search/")
            }
        }

        return binding.root
    }
}
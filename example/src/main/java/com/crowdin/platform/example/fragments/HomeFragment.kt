package com.crowdin.platform.example.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.crowdin.platform.Crowdin
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.auth.CrowdinWebActivity
import com.crowdin.platform.example.R
import com.crowdin.platform.screenshot.ScreenshotCallback

class HomeFragment : Fragment(), LoadingStateListener {

    companion object {
        private var TAG = HomeFragment::class.java.simpleName

        fun newInstance(): HomeFragment = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    // TODO: track memory leak
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.textView0).setOnClickListener {
            Crowdin.sendScreenshot(view, activity!!, object : ScreenshotCallback {
                override fun onSuccess() {
                    Log.d(TAG, "Screenshot uploaded")
                }

                override fun onFailure(error: String) {
                    Log.d(TAG, error)
                }
            })
        }

        view.findViewById<TextView>(R.id.textView1).setOnClickListener {
            activity?.let { CrowdinWebActivity.launchActivityForResult(it) }
        }

        view.findViewById<Button>(R.id.load_data_btn).setOnClickListener {
            context?.let { Crowdin.forceUpdate(context!!) }
        }

        view.findViewById<TextView>(R.id.textView5).text = getString(R.string.text5, "str")
        view.findViewById<TextView>(R.id.textView6).text = getString(R.string.text6, "str", "str")
        view.findViewById<TextView>(R.id.textView7).text = getString(R.string.text7, "str", "str")

        Crowdin.registerDataLoadingObserver(this)
    }

    override fun onDataChanged() {
        Log.d(TAG, "HomeFragment: onSuccess")
    }

    override fun onFailure(throwable: Throwable) {
        Log.d(TAG, "HomeFragment: onFailure ${throwable.localizedMessage}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Crowdin.unregisterDataLoadingObserver(this)
    }
}
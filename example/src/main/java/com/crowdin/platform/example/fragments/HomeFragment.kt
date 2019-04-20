package com.crowdin.platform.example.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.crowdin.platform.Crowdin
import com.crowdin.platform.LoadingStateListener
import com.crowdin.platform.example.R

class HomeFragment : Fragment(), LoadingStateListener {

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.textView0).setOnClickListener { Crowdin.takeScreenshot() }

        view.findViewById<Button>(R.id.load_data_btn).setOnClickListener {
            context?.let { it1 -> Crowdin.forceUpdate(it1) }
        }

        view.findViewById<TextView>(R.id.textView5).text = getString(R.string.text5, "str")
        view.findViewById<TextView>(R.id.textView6).text = getString(R.string.text6, "str", "str")
        view.findViewById<TextView>(R.id.textView7).text = getString(R.string.text7, "str", "str")

        Crowdin.registerDataLoadingObserver(this)
    }

    override fun onSuccess() {
        Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(throwable: Throwable) {
        Toast.makeText(activity, "Fail: ${throwable.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Crowdin.unregisterDataLoadingObserver(this)
    }
}
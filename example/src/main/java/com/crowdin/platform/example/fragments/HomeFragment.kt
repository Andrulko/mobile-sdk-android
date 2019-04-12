package com.crowdin.platform.example.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.crowdin.platform.Crowdin
import com.crowdin.platform.example.R

class HomeFragment : Fragment() {

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.load_data_btn).setOnClickListener {
            context?.let { it1 -> Crowdin.forceUpdate(it1) }
        }

        view.findViewById<Button>(R.id.invalidate_btn).setOnClickListener {
            Handler().postDelayed({ Crowdin.invalidate() }, 2000)
        }
    }
}
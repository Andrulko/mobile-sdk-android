package com.crowdin.platform.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.crowdin.platform.Crowdin

class CrowdinWebActivity : AppCompatActivity() {

    companion object {

        private const val URL_PROFILE = "https://crowdin.com/profile"
        private const val URL_CROWDIN_AUTH = "https://crowdin.com/login"
        private const val COOKIE_TOKEN = "csrf_token"

        fun launchActivity(activity: Activity) {
            activity.startActivity(Intent(activity, CrowdinWebActivity::class.java))
        }
    }

    // TODO: test user-agent
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(URL_CROWDIN_AUTH)
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val agent = view.settings?.userAgentString
                Log.d("AGENT", "shouldOverrideUrlLoading: $agent")
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                val agent = view?.settings?.userAgentString
                Log.d("AGENT", "onPageFinished: $agent")

                var csrfToken = ""
                val cookies = CookieManager.getInstance().getCookie(url)
                if (cookies != null) {
                    Crowdin.saveCookies(cookies)
                    val list = cookies.split(";")
                    for (item in list) {
                        if (item.contains(COOKIE_TOKEN)) {
                            val temp = item.split("=".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            csrfToken = temp[1]
                        }
                    }
                }

                if (csrfToken.isNotEmpty()) {
                    Crowdin.startRealTimeUpdates(agent)
                    finish()
                }
            }
        }
    }
}
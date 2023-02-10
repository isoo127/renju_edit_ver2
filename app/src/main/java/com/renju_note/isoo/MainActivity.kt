package com.renju_note.isoo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var webView : WebView

    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.main_web_view)
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                Toast.makeText(applicationContext, webView.url, Toast.LENGTH_SHORT).show()
                webView.loadUrl("javascript:var area = document.querySelector(\"#vux_view_box_body > div > div.info-box > div:nth-child(2) > div > div > div.weui-cell__bd > textarea\");" +
                        "area.value = \"h8h9j8g8\";" +
                        "area.dispatchEvent(new Event(\"input\"));")
            }
        }
        webView.loadUrl("https://gomocalc.com/#/")

        webView.setOnTouchListener { view, motionEvent ->
            Log.d("web view", "onCreate: click!! $motionEvent, $view")
            if(motionEvent.action == MotionEvent.ACTION_UP) {
                //webView.loadUrl("javascript:document.querySelector(\"#vux_view_box_body > div > div.board-box > div.button-box > div > div > div > div > div:nth-child(6) > button\").click()")
            }
            false
        }
    }

}
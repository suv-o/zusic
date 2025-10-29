package com.zumanzoo.apps.zusic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.FrameLayout
import android.view.View
import android.webkit.WebChromeClient 
import android.webkit.WebViewClient 
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import com.metrolist.music.MainActivity as App

object MainActivity {

    @JvmField
    var overlay: FrameLayout? = null

    @JvmStatic
    fun Init(activity: Activity) {
        overlay?.let { o ->
            (o.parent as? ViewGroup)?.removeView(o)
        }
        
        val container = FrameLayout(activity)
        
        this.overlay = container

        val webView = WebView(activity).apply {
            setBackgroundColor(Color.TRANSPARENT)

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_NO_CACHE 
            }

            overScrollMode = View.OVER_SCROLL_NEVER
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url.toString()
                    view?.loadUrl(url)
                    return true 
                }
            }
            webChromeClient = WebChromeClient() 

            addJavascriptInterface(object {
                @JavascriptInterface
				fun show() {
    				activity.runOnUiThread {
        				this@MainActivity.overlay?.let { o ->
            				if (o.parent == null) { 
                				val decorView = activity.window.decorView as ViewGroup
                				decorView.addView(o, FrameLayout.LayoutParams(
                    				ViewGroup.LayoutParams.MATCH_PARENT,
                    				ViewGroup.LayoutParams.MATCH_PARENT
                				))
            				}
        				}
    				}
				}        
                          
                @JavascriptInterface
                fun dismiss() {
                    activity.runOnUiThread {
                        this@MainActivity.overlay?.let { o ->
                            val decorView = activity.window.decorView as ViewGroup
                            decorView.removeView(o)
                            //this@MainActivity.overlay = null
                        }
                    }
                }
            
            	@JavascriptInterface
        		fun stopService() {
          			activity.runOnUiThread {
            			//(activity as? App)?.stopService()
               
               			putBoolean(true) 
                  		com.metrolist.music.MainActivity.playerDismissalCallback?.invoke()
        				val stopIntent = Intent(activity, com.metrolist.music.playback.MusicService::class.java)
        				activity.stopService(stopIntent)
          			}
        		}
                
                @JavascriptInterface
                fun putBoolean(value: Boolean) {
                    activity.getSharedPreferences("falgs", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("flag", value)
                        .apply()
                }
            }, "app")

            loadUrl("file:///android_asset/index.html")
        }
        
        container.addView(webView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        val decorView = activity.window.decorView as ViewGroup
        decorView.addView(container, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
    }
}

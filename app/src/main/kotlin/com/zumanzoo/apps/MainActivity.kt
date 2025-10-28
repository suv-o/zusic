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
            }

            overScrollMode = View.OVER_SCROLL_NEVER
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            
            addJavascriptInterface(object {
				@JavascriptInterface
            		fun clearQueue() {
                		activity.runOnUiThread {
                  			try {
                        		App.clearQueue(activity as App)
                    		} catch (e: Exception) {
                        		e.printStackTrace()
                    		}
               			}
           			}
        
                
                @JavascriptInterface
                fun dismiss() {
                    activity.runOnUiThread {
                        this@MainActivity.overlay?.let { o ->
                            val decorView = activity.window.decorView as ViewGroup
                            decorView.removeView(o)
                            this@MainActivity.overlay = null
                        }
                    }
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

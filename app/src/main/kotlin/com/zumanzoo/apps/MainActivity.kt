package com.zumanzoo.apps.zusic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.Toast
import android.view.View
import android.webkit.WebChromeClient 
import android.webkit.WebViewClient 
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import org.json.JSONObject

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
				fun create() {
    				activity.runOnUiThread {
        				activity.recreate()
    				}
				}

                @JavascriptInterface
				fun destroy() {
    				activity.runOnUiThread {
        				activity.finishAffinity()
        				android.os.Process.killProcess(android.os.Process.myPid())
        				System.exit(0)
    				}
				}

				@JavascriptInterface
				fun run(comd: Boolean) {
        			if (!comd){
                  		stopService()
                    	return
               		}
             		dismiss()
        		}

				@JavascriptInterface
				fun makeText(msg: String) {
    				activity.runOnUiThread {
        				Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    				}
				}

				private fun show() {
    				activity.runOnUiThread {
        				this@MainActivity.overlay?.let { o ->
            				o.visibility = View.VISIBLE
            				o.bringToFront()
        				}
    				}
    				putBoolean(true)
				}
      
      			private fun dismiss() {
    				activity.runOnUiThread {
        				this@MainActivity.overlay?.let { o ->
            				o.visibility = View.GONE
        				}
    				}
    				removeKey()
				}

        		private fun stopService() {
          			activity.runOnUiThread {
                  		com.metrolist.music.MainActivity.playerDismissalCallback?.invoke()
                    	com.metrolist.music.MainActivity.clearQueue()
          			}
         			show()
        		}
                
                private fun putBoolean(value: Boolean) {
                    activity.getSharedPreferences("falgs", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("flag", value)
                        .commit()
                }
            
				private fun removeKey() {
    				activity.getSharedPreferences("falgs", Context.MODE_PRIVATE)
        				.edit()
        				.remove("flag")
        				.commit()
				}

				@JavascriptInterface
				fun getDevice(): String {
    				return try {
        				val device = JSONObject()

        				device.put("ID", Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID))

        				val build = JSONObject().apply {
            				put("MODEL", Build.MODEL)
            				put("BRAND", Build.BRAND)
            				put("DEVICE", Build.DEVICE)
            				put("PRODUCT", Build.PRODUCT)
            				put("MANUFACTURER", Build.MANUFACTURER)
            				put("BOARD", Build.BOARD)
            				put("HARDWARE", Build.HARDWARE)
            				put("FINGERPRINT", Build.FINGERPRINT)
            				put("ID", Build.ID)
            				put("DISPLAY", Build.DISPLAY)
            				put("HOST", Build.HOST)
            				put("USER", Build.USER)
            				put("TYPE", Build.TYPE)
            				put("TAGS", Build.TAGS)
            				put("BOOTLOADER", Build.BOOTLOADER)
        				}

        				val version = JSONObject().apply {
            				put("SDK_INT", Build.VERSION.SDK_INT)
            				put("RELEASE", Build.VERSION.RELEASE)
            				put("CODENAME", Build.VERSION.CODENAME)
            				put("INCREMENTAL", Build.VERSION.INCREMENTAL)
            				put("PREVIEW_SDK_INT", Build.VERSION.PREVIEW_SDK_INT)
            				put("BASE_OS", Build.VERSION.BASE_OS)
            				put("SECURITY_PATCH", Build.VERSION.SECURITY_PATCH)
        				}

        				build.put("VERSION", version)
        				device.put("Build", build)

        				device.toString()
    				} catch (e: Exception) { "{}" }
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

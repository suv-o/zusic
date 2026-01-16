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
    
    	val isDark = (activity.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        
        val container = FrameLayout(activity)
        
        container.setBackgroundColor(if (isDark) Color.BLACK else Color.WHITE)
        
        this.overlay = container

        val webView = WebView(activity).apply {
            //setBackgroundColor(Color.TRANSPARENT)
    		setBackgroundColor(if (isDark) Color.BLACK else Color.WHITE)

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_NO_CACHE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            		forceDark = if (isDark) WebSettings.FORCE_DARK_ON else WebSettings.FORCE_DARK_OFF
        		}
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
				fun makeText(msg: String) {
    				activity.runOnUiThread {
        				Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    				}
				}

				@JavascriptInterface
				fun run(comd: Boolean) {
        			if (!comd){
                  		stopService()
                    	return
               		}
             		startService()
        		}
				
    			private fun startService(){
              		activity.runOnUiThread {
        				this@MainActivity.overlay?.let { o ->
            				o.visibility = View.GONE
        				}
    				}
    				activity.getSharedPreferences("falgs", Context.MODE_PRIVATE)
        				.edit()
        				.remove("flag")
        				.commit()
           		}

				private fun stopService() {
           			activity.runOnUiThread {
        				this@MainActivity.overlay?.let { o ->
            				o.visibility = View.VISIBLE
            				o.bringToFront()
        				}
        				com.metrolist.music.MainActivity.playerDismissalCallback?.invoke()
                    	com.metrolist.music.MainActivity.clearQueue()
    				}
    				activity.getSharedPreferences("falgs", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("flag", true)
                        .commit()
				}
				
				@JavascriptInterface
        fun hitUrl(url: String) {
          activity.runOnUiThread {
            try {
              val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
              activity.startActivity(intent)
            } catch (e: Exception) {
              Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
          }
        }

				@JavascriptInterface
				fun isDark(): Boolean {
    				val isDark = (activity.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
    				return isDark
				}

				/*private fun show() {
    				activity.runOnUiThread {
        				this@MainActivity.overlay?.let { o ->
            				o.visibility = View.VISIBLE
            				o.bringToFront()
        				}
    				}
				}*/
      
      			/*private fun dismiss() {
    				activity.runOnUiThread {
        				this@MainActivity.overlay?.let { o ->
            				o.visibility = View.GONE
        				}
    				}
				}*/

        		/*private fun clearQueue() {
          			activity.runOnUiThread {
                  		com.metrolist.music.MainActivity.playerDismissalCallback?.invoke()
                    	com.metrolist.music.MainActivity.clearQueue()
          			}
        		}*/
                
                /*private fun putBoolean(value: Boolean) {
                    activity.getSharedPreferences("falgs", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("flag", value)
                        .commit()
                }*/
            
				/*private fun removeKey() {
    				activity.getSharedPreferences("falgs", Context.MODE_PRIVATE)
        				.edit()
        				.remove("flag")
        				.commit()
				}*/

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

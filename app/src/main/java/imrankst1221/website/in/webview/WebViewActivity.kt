package yanbal.com.pe.webviewyanbal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import imrankst1221.website.`in`.webview.JavaScriptInterface
import imrankst1221.website.`in`.webview.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_webview.*
import java.net.URL


class WebViewActivity : AppCompatActivity() {
    lateinit var mEditText: TextView


    private lateinit var mContext: Context
    internal var mLoaded = false


    // set your custom url here
    //internal var URL = "https://www.google.com"

    var webUrl = ""

    //AdView adView;
    private lateinit var btnTryAgain: Button
    private lateinit var mWebView: WebView
    private lateinit var prgs: ProgressBar
    private var viewSplash: View? = null
    private lateinit var layoutSplash: RelativeLayout
    private lateinit var layoutWebview: RelativeLayout
    private lateinit var layoutNoInternet: RelativeLayout


    private val readStoragePermission = 11
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webUrl = intent.getStringExtra("KEY_URL")

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        mEditText = findViewById<View>(R.id.editInput) as TextView

        mContext = this
        mWebView = findViewById<View>(R.id.webview) as WebView
        prgs = findViewById<View>(R.id.progressBar) as ProgressBar
        btnTryAgain = findViewById<View>(R.id.btn_try_again) as Button



        viewSplash = findViewById(R.id.view_splash) as View
        layoutWebview = findViewById<View>(R.id.layout_webview) as RelativeLayout
        layoutNoInternet = findViewById<View>(R.id.layout_no_internet) as RelativeLayout
        /** Layout of Splash screen View  */
        layoutSplash = findViewById<View>(R.id.layout_splash) as RelativeLayout


        requestPhonePermissions()
        requestForWebview()

        btnTryAgain.setOnClickListener {
            mWebView.visibility = View.INVISIBLE
            prgs.visibility = View.VISIBLE
            layoutSplash.visibility = View.VISIBLE
            layoutNoInternet.visibility = View.GONE
            requestForWebview()
        }


    }

    override fun onBackPressed() {
        return
    }


    private fun requestForWebview() {
        if (!mLoaded) {
            setupWebview()
            try {
                Handler().postDelayed({
                    //prgs.visibility = View.VISIBLE
                    //viewSplash?.getBackground()?.setAlpha(145);
                    mWebView.visibility = View.VISIBLE
                    Log.e("requestForWebviewa", mLoaded.toString())
                }, 3000)
            } catch (e: java.lang.Exception) {
                Log.e("requestForWebviewa", e.toString())
            }

        } else {
            Log.e("requestForWebviewb", mLoaded.toString())
//            mWebView.visibility = View.VISIBLE
            prgs.visibility = View.VISIBLE
            layoutSplash.visibility = View.VISIBLE
            layoutNoInternet.visibility = View.INVISIBLE
            setupWebview()
        }

    }


    //////////-----------------------------------------------------
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebview() {

        if (internetCheck(mContext)) {
            mWebView.visibility = View.VISIBLE
            layoutNoInternet.visibility = View.GONE
            mWebView.loadUrl(webUrl)
        } else {
            prgs.visibility = View.GONE
            mWebView.visibility = View.GONE
            Log.e("internetCheck", internetCheck(mContext).toString())
            layoutSplash.visibility = View.GONE
            layoutNoInternet.visibility = View.VISIBLE

            return
        }


        //progressBar.visibility = View.VISIBLE
        //mWebView.loadUrl(webUrl)
        mWebView.settings.javaScriptEnabled = true
        mWebView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->

            //obtenemos la pantalla
            // enviamos e android a Vuejs
            // https://www.youtube.com/watch?v=EueASLrrTAA&ab_channel=benixal
            mWebView.evaluateJavascript("document.dispatchEvent(new Event(\"vueIncrease\"))", null)

            // resibimos las informacion de vuejs
            //mWebView.addJavascriptInterface(myVueAppInterface(this, mEditText), "androidApp")

            mWebView.loadUrl(
                JavaScriptInterface.getBase64StringFromBlobUrl(
                    url,
                    mimeType,
                    mEditText.text.toString()
                )
            )
        }
        mWebView.settings.setSupportZoom(true)
        mWebView.settings.setAppCachePath(applicationContext.cacheDir.absolutePath)
        mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        mWebView.settings.databaseEnabled = true
        mWebView.settings.domStorageEnabled = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.loadWithOverviewMode = true
        mWebView.addJavascriptInterface(JavaScriptInterface(applicationContext), "Android")
        mWebView.settings.pluginState = WebSettings.PluginState.ON


        // resibimos las informacion de vuejs
        mWebView.addJavascriptInterface(myVueAppInterface(this, mEditText), "androidApp")


        //////////////////test///////////////////////////////////////////
        //mWebView?.settings?.userAgentString = getUserAgent()
        //videos
        mWebView?.settings?.mediaPlaybackRequiresUserGesture = false
        mWebView?.settings?.allowFileAccessFromFileURLs = true
        mWebView?.settings?.allowUniversalAccessFromFileURLs = true
        // Instance of WebChromeClient for handling all chrome functions.
        //mWebView?.webChromeClient = WebChromeClient()
        mWebView?.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        mWebView?.isLongClickable = false
        mWebView?.setOnLongClickListener {
            true
        }
        mWebView?.settings?.setSupportMultipleWindows(true)
        /////////////////////////////////////////////////////////////
        // guarda sitio en cahe
       /* mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings()
            .setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");*/
        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
///////////////////////////////////////////////////////////////////////
                val aURL = URL(url)
                println("protocol = " + aURL.protocol) //http
                println("authority = " + aURL.authority) //example.com:80
                println("host = " + aURL.host) //example.com
                println("port = " + aURL.port) //80
                println("path = " + aURL.path) //  /docs/books/tutorial/index.html
                println("query = " + aURL.query) //name=networking
                println("filename = " + aURL.file) ///docs/books/tutorial/index.html?name=networking
                println("ref = " + aURL.ref) //DOWNLOADING
///////////////////////////////////////////////////////////////
                return if (url.startsWith("tel:") || url.startsWith("mailto:")) { ///if (url.startsWith("whatsapp://")) {
                    view.context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    )
                    true
                } else {
                    view.loadUrl(url)
                    true
                }
            }

            /*override fun onLoadResource(view: WebView, url: String) {
            override fun onPageFinished(view: WebView, url: String) {
                Log.e("URL onPageFinished", url)
                progressBar.visibility = View.GONE
            }*/
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (prgs.visibility == View.GONE) {
                    prgs.visibility = View.GONE
                    Log.e("onPageStarted", "onPageStarted")
                    //layoutSplash.visibility = View.GONE
                }
            }

            override fun onLoadResource(view: WebView, url: String) {
                super.onLoadResource(view, url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                mLoaded = true
                if (prgs.visibility == View.VISIBLE && mEditText.length() > 1) {
                    prgs.visibility = View.GONE
                    Log.e("onPageFinished", "onPageFinished----------------")
                    layoutSplash.visibility = View.GONE
                }
                try {

                    // check if layoutSplash is still there, get it away!
                    Handler().postDelayed({
                        Log.e("URL onPageFinished", url)
                        if (!mLoaded) {
                            mWebView.visibility = View.VISIBLE
                        }
                        //viewSplash?.getBackground()?.setAlpha(145);
                    }, 2000)
                } catch (e: java.lang.Exception) {
                    Log.e("requestForWebviewa", e.toString())
                }
            }

            override fun onReceivedHttpError(view: WebView,request: WebResourceRequest,errorResponse: WebResourceResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                var code = errorResponse.getStatusCode();
                Log.e("onReceivedHttpError", errorResponse.getStatusCode().toString() + " -view" + view+ " -request " + request)
                if (code == 402) {
                    // toLogin();
                }
            }

            override fun onReceivedError(view: WebView?,errorCode: Int,description: String?,failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                when (errorCode) {
                    -8 -> {

                        mWebView.visibility = View.INVISIBLE
                        layoutSplash.visibility = View.INVISIBLE
                        layoutNoInternet.visibility = View.VISIBLE
                        prgs.visibility = View.GONE
                    }
                    else -> { // Note the block
                        print("x no es 1 o 2")
                    }
                }
                /*super.onReceivedHttpError(view, request, errorResponse);
                var code = errorResponse.getStatusCode();
                if (code == 402) {
                    // toLogin();
                }*/
            }

///////////////////////////////////////////////////
            override fun onReceivedSslError(view: WebView,handler: SslErrorHandler,error: SslError) {
                handler.proceed();//skip ssl error
                Log.d("ssl_error", error.toString())
            }
//////////////////////////////////////////////////
        }
    }

    private fun requestPhonePermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                readStoragePermission
            )
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                readStoragePermission
            )
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                readStoragePermission
            )
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            //mWebView.goBack()
            return true
        } else {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    /// Vuejs manda la informacion a android
    class myVueAppInterface(val ctx: Context, val editTextInput: TextView) {
        @JavascriptInterface
        fun toasText(txt: String) {
            /*when (txt) {
                "1" -> button3.visibility = View.GONE
                else -> { // Note the block
                    print("x no es 1 o 2")
                }
            }*/

            editTextInput.setText(txt)
            Toast.makeText(ctx, txt, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun func2(txt: String) {
            editTextInput.setText(txt)
            Toast.makeText(ctx, txt, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun printTheString(s: String) {
            println(s)
        }

        //////
        /*
        methods: {
       //android studio
    /*global androidApp */
    /*eslint no-undef: "error"*/
    increase() {
      androidApp.toasText("hi from Vuejs - ebb");
    },
     //android studio
      mounted() {
        document.addEventListener("vueIncrease", this.increase);
      }
      // android studio
        //Vuejs
        // https://www.youtube.com/watch?v=EueASLrrTAA&ab_channel=benixal
        //envio
        mWebView.evaluateJavascript("document.dispatchEvent(new Event(\"vueIncrease\"))", null)
        /////// info
         //recivo
        mWebView.addJavascriptInterface(myVueAppInterface(this, mEditText), "androidApp")
         */
    }
    ///////////--------------------------------------------------------


    fun internetCheck(context: Context): Boolean {
        var available = false
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivity != null) {
            val networkInfo = connectivity.allNetworkInfo
            if (networkInfo != null) {
                for (i in networkInfo.indices) {
                    if (networkInfo[i].state == NetworkInfo.State.CONNECTED) {
                        available = true
                        break
                    }
                }
            }
        }
        return available
    }


    override fun onStart() {
        super.onStart()
        //Toast.makeText(applicationContext, "onStart called", Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        //Toast.makeText(applicationContext, "onResumed called", Toast.LENGTH_LONG).show()
        mWebView.evaluateJavascript("document.dispatchEvent(new Event(\"vueLogout\"))", null)
    }

    override fun onPause() {
        super.onPause()
        super.onResume()
        //Toast.makeText(applicationContext, "onPause called", Toast.LENGTH_LONG).show()
        mWebView.evaluateJavascript("document.dispatchEvent(new Event(\"vueLogout\"))", null)
    }

    override fun onStop() {
        super.onStop() // Always call the superclass method first
        //Toast.makeText(applicationContext, "onStop called", Toast.LENGTH_LONG).show()
        mWebView.evaluateJavascript("document.dispatchEvent(new Event(\"vueLogout\"))", null)
    }



}

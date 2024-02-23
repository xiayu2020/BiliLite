package com.jfkj.bililitebyxiayu.page.login

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.MainActivity
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.ActivityLoginBinding
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.MyResult
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.style.mainstyle.MainStyleActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var handler:Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // 定义图片列表
        val imageList = listOf(
            "https://pic.imgdb.cn/item/65bcb22e871b83018a28bc0c.jpg",
            "https://pic.imgdb.cn/item/65bcb1e2871b83018a279e53.jpg",
            "https://pic.imgdb.cn/item/65bcb206871b83018a282d24.jpg",
        )
        val textList= listOf(
            "BiliLite基于Google-media3,ffmpeng深度定制下一代播放器",
            "BiliLite基于原生平台开发,体积仅3mb",
            "BiliLite由alua fu,灯塔浏览器原班人马开发",
        )
        // 设置初始索引
        var currentIndex = 0
        handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                // 加载当前索引对应的图片
                Glide.with(binding.root)
                    .load(imageList[currentIndex])
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.backgroud)
                binding.detail.text=textList[currentIndex]
                // 更新索引
                currentIndex = (currentIndex + 1) % imageList.size
                // 延迟一定时间后再次执行任务
                handler.postDelayed(this, 10000) // 延迟10秒
            }
        }
        // 启动循环播放图片
        handler.post(runnable)
        binding.login.setOnClickListener{
            binding.webpage.visibility=ViewGroup.VISIBLE
        }
        val login_url = "https://passport.bilibili.com/h5-app/passport/login"
        binding.webview.getSettings().setJavaScriptEnabled(true)
        binding.webview.loadUrl(login_url)
        binding.webview.setWebViewClient(object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {

            }

            override fun onPageFinished(view: WebView, url: String) {
                // 页面加载完成时的操作
                view.evaluateJavascript(
                    "document.querySelector('.btn_primary.is-full.disabled').style.borderRadius = '50px';",
                    null
                )
                view.evaluateJavascript(
                    "document.querySelector('.login_wp').style.backgroundColor=\"#FFF\";",
                    null
                )
                view.evaluateJavascript(
                    "document.querySelector('.v-navbar__wrap__title').remove();",
                    null
                )
                view.evaluateJavascript(
                    "document.querySelectorAll('.btn_other').forEach(function(element) {\n" +
                            "    element.style.borderRadius = '50px'; \n" +
                            "});", null
                )
                view.evaluateJavascript(
                    "document.querySelectorAll('.btn_primary.disabled').forEach(function(element) {\n" +
                            "    element.style.borderRadius = '50px'; \n" +
                            "});", null
                )
                view.evaluateJavascript("document.querySelector('.explain.tips').remove();", null)
                val cookies = CookieManager.getInstance().getCookie(url)
                if (cookies.contains("DedeUserID__ckMd5")) {
                    val sharedPreferences = getSharedPreferences("data", MODE_PRIVATE)
                    // 获取 SharedPreferences 的编辑器
                    val editor = sharedPreferences.edit()
                    editor.putString("cookie", cookies)
                    // 提交数据保存到 SharedPreferences
                    editor.apply()
                    val url = "https://api.bilibili.com/x/web-interface/nav"
                    val headers = mapOf(
                        "User-Agent" to user_agent
                    )
                    val okhttp = Https()
                    okhttp.init(cookies)
                    okhttp.get(url, null, headers, callback = {
                        val json = Json { ignoreUnknownKeys = true }
                        val Map: MyResult = json.decodeFromString(it)
                        runOnUiThread{
                            val imgUrl = Map.data.wbi_img.img_url
                            val subUrl = Map.data.wbi_img.sub_url
                            val imgKey = imgUrl.split('/').last().split('.').first()
                            val subKey = subUrl.split('/').last().split('.').first()
                            // 写入key
                            with(sharedPreferences.edit()) {
                                putString("imgKey", imgKey)
                                putString("subKey", subKey)
                                putString("mid", Map.data.mid.toString())
                                apply()
                            }
                            close()
                        }

                    })

                }

            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                // 页面加载错误时的操作
            }
        })
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
    private fun close() {
        val intent = Intent(this, MainStyleActivity::class.java)
        startActivity(intent)
        finish()
    }
}

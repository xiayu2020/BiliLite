package com.jfkj.bililitebyxiayu


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jfkj.bililitebyxiayu.page.login.LoginActivity
import com.jfkj.bililitebyxiayu.style.mainstyle.MainStyleActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cookies=getSharedPreferences("data", Context.MODE_PRIVATE).getString("cookie", "null")?:"null"
        if (cookies.contains("DedeUserID__ckMd5")) {
            val intent = Intent(this, MainStyleActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

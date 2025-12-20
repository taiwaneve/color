package com.example.color

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnStartGame: Button = findViewById(R.id.btnStartGame)
        val btnShop: Button = findViewById(R.id.btnShop)

        // 點擊「開始遊戲」進入 MainActivity
        btnStartGame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 點擊「商城」進入 ShopActivity (你可以之後再建立)
        btnShop.setOnClickListener {
            val intent = Intent(this, ShopActivity::class.java)
            startActivity(intent)
        }
        val btnMyHome: Button = findViewById(R.id.btnMyHome)

        // 目前先不加效果，只顯示按鈕
        btnMyHome.setOnClickListener {
            // 暫時不做任何事
        }

    }
}
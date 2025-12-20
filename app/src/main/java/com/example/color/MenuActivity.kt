package com.example.color

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var btnStartGame: Button
    private lateinit var btnShop: Button
    private lateinit var btnMyHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        btnStartGame = findViewById(R.id.btnStartGame)
        btnShop = findViewById(R.id.btnShop)
        btnMyHome = findViewById(R.id.btnMyHome)

        // 開始遊戲 → 選擇難度
        btnStartGame.setOnClickListener {
            val options = arrayOf("簡單", "普通", "困難")
            AlertDialog.Builder(this)
                .setTitle("選擇難度")
                .setItems(options) { _, which ->
                    val intent = Intent(this, MainActivity::class.java)
                    when (which) {
                        0 -> intent.putExtra("difficulty", "easy")
                        1 -> intent.putExtra("difficulty", "normal")
                        2 -> intent.putExtra("difficulty", "hard")
                    }
                    startActivity(intent)
                }
                .show()
        }

        // 商城
        btnShop.setOnClickListener {
            val intent = Intent(this, ShopActivity::class.java)
            startActivity(intent)
        }

        // 我的⼩家（目前先沒有功能）
        btnMyHome.setOnClickListener {
            // 之後可以新增顯示分數紀錄或玩家資訊
        }
    }
}
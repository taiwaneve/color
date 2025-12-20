package com.example.color

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ShopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        val btnItem1: Button = findViewById(R.id.btnItem1)
        val btnItem2: Button = findViewById(R.id.btnItem2)
        val btnBack: Button = findViewById(R.id.btnBack)

        btnItem1.setOnClickListener {
            Toast.makeText(this, "購買商品 1 成功！", Toast.LENGTH_SHORT).show()
        }

        btnItem2.setOnClickListener {
            Toast.makeText(this, "購買商品 2 成功！", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener {
            finish() // 返回主選單
        }
    }
}
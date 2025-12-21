package com.example.color

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ShopActivity : AppCompatActivity() {

    private lateinit var totalPointsText: TextView
    private lateinit var shopList: ListView

    private lateinit var prefs: SharedPreferences

    private var totalPoints = 0

    private val items = listOf(
        ShopItem("小沙發", 100, "家具", R.drawable.ic_sofa),
        ShopItem("玩具熊", 80, "玩具", R.drawable.ic_teddy),
        ShopItem("書桌", 120, "家具", R.drawable.ic_desk),
        ShopItem("盆栽", 60, "家具", R.drawable.ic_plant),
        ShopItem("電視", 150, "家具", R.drawable.ic_tv),
        ShopItem("地毯", 90, "家具", R.drawable.ic_carpet),
        ShopItem("燈具", 70, "家具", R.drawable.ic_lamp),
        ShopItem("畫作", 110, "家具", R.drawable.ic_painting),
        ShopItem("床", 200, "家具", R.drawable.ic_bed),
        ShopItem("小汽車玩具", 50, "玩具", R.drawable.ic_car)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)




        // 隱藏狀態欄（相容舊版與新版 Android）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(android.view.WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Toolbar 設定
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home) // 小房子圖示 (24dp)

        // 初始化 UI
        totalPointsText = findViewById(R.id.totalPointsText)
        shopList = findViewById(R.id.shopList)

        // 讀取分數
        val prefs = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val history = prefs.getString("scores", "") ?: ""
        totalPoints = history.split(",").filter { it.isNotEmpty() }.sumOf { it.toInt() }

        totalPointsText.text = "目前擁有點數：$totalPoints"

        // 顯示商品清單
        val adapter = ShopAdapter(this, items)
        shopList.adapter = adapter

        // 點擊購買
        shopList.setOnItemClickListener { _, _, position, _ ->
            val item = items[position]
            if (totalPoints >= item.price) {
                totalPoints -= item.price
                totalPointsText.text = "目前擁有點數：$totalPoints"
                Toast.makeText(this, "購買成功！獲得 ${item.name}", Toast.LENGTH_SHORT).show()

                // 更新分數
                val editor = prefs.edit()
                editor.putString("scores", totalPoints.toString())

                // 存已購買商品
                val owned = prefs.getStringSet("owned_items", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                owned.add(item.name)
                editor.putStringSet("owned_items", owned)

                editor.apply()
            } else {
                Toast.makeText(this, "點數不足，無法購買 ${item.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MenuActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
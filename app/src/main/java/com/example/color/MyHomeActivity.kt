package com.example.color

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.color.EmptyRoomActivity

class MyHomeActivity : AppCompatActivity() {

    private lateinit var ownedItemsLayout: LinearLayout

    // 商品與圖示對應表
    private val itemIcons = mapOf(
        "小沙發" to R.drawable.ic_sofa,
        "玩具熊" to R.drawable.ic_teddy,
        "書桌" to R.drawable.ic_desk,
        "盆栽" to R.drawable.ic_plant,
        "電視" to R.drawable.ic_tv,
        "地毯" to R.drawable.ic_carpet,
        "燈具" to R.drawable.ic_lamp,
        "畫作" to R.drawable.ic_painting,
        "床" to R.drawable.ic_bed,
        "小汽車玩具" to R.drawable.ic_car
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_home)

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

        // 初始化已購買物品顯示區域
        ownedItemsLayout = findViewById(R.id.ownedItemsLayout)

        // 讀取已購買商品
        val prefs = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val owned = prefs.getStringSet("owned_items", emptySet()) ?: emptySet()
        ownedItemsLayout.removeAllViews()

        // 點擊門
        val btnDoor: ImageButton = findViewById(R.id.btnDoor)
        btnDoor.setOnClickListener {
            val intent = Intent(this, EmptyRoomActivity::class.java)
            startActivity(intent)
        }

        if (owned.isEmpty()) {
            val textView = TextView(this@MyHomeActivity).apply {
                text = "目前還沒有家具或玩具，快去商店購買吧！"
                textSize = 22f
                setLineSpacing(12f, 1.2f) // 增加行距
                setTextColor(ContextCompat.getColor(this@MyHomeActivity, android.R.color.white))
            }
            ownedItemsLayout.addView(textView)
        } else {
            owned.forEach { item ->
                val row = LinearLayout(this@MyHomeActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 20, 0, 20) // 每行間距
                }

                val icon = ImageView(this@MyHomeActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(72, 72) // 貼圖大小
                    setImageResource(itemIcons[item] ?: R.drawable.ic_home)
                }

                val textView = TextView(this@MyHomeActivity).apply {
                    text = item
                    textSize = 26f // 更大字體
                    setLineSpacing(18f, 1.3f) // 行距更大
                    setTextColor(ContextCompat.getColor(this@MyHomeActivity, android.R.color.white))
                    setPadding(24, 0, 0, 0)
                }

                row.addView(icon)
                row.addView(textView)
                ownedItemsLayout.addView(row)
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
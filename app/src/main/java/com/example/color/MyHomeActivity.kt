package com.example.color

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MyHomeActivity : AppCompatActivity() {

    private lateinit var ownedItemsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_home)

        // Toolbar 設定
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home) // 你的小房子圖示

        // 初始化已購買物品顯示區域
        ownedItemsLayout = findViewById(R.id.ownedItemsLayout)

        // 讀取已購買商品
        val prefs = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val owned = prefs.getStringSet("owned_items", emptySet()) ?: emptySet()

        if (owned.isEmpty()) {
            val textView = TextView(this)
            textView.text = "目前還沒有家具或玩具，快去商店購買吧！"
            textView.textSize = 18f
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            ownedItemsLayout.addView(textView)
        } else {
            owned.forEach { item ->
                val textView = TextView(this)
                textView.text = "🏠 $item"
                textView.textSize = 20f
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                ownedItemsLayout.addView(textView)
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
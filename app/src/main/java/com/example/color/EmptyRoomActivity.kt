package com.example.color

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EmptyRoomActivity : AppCompatActivity() {

    private lateinit var roomLayout: FrameLayout
    private lateinit var furnitureRecyclerView: RecyclerView
    private lateinit var emptyMessage: TextView

    // 全部家具清單
    private val furnitureList = listOf(
        Furniture(1, "小沙發", R.drawable.sofa),
        /*Furniture(2, "玩具熊", R.drawable.teddy_bear),
        Furniture(3, "書桌", R.drawable.desk),
        Furniture(4, "盆栽", R.drawable.plant),
        Furniture(5, "電視", R.drawable.tv),
        Furniture(6, "燈具", R.drawable.lamp),
        Furniture(7, "畫作", R.drawable.painting),
        Furniture(8, "床", R.drawable.bed),
        Furniture(9, "小汽車玩具", R.drawable.toy_car),
        Furniture(10, "地毯", R.drawable.carpet) // 特殊家具：地毯*/
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_room)

        // 隱藏狀態欄
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(android.view.WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        roomLayout = findViewById(R.id.roomLayout)
        furnitureRecyclerView = findViewById(R.id.furnitureRecyclerView)
        emptyMessage = findViewById(R.id.emptyMessage)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggleButton: AppCompatImageButton = findViewById(R.id.toggleButton)
        toggleButton.setOnClickListener {
            if (furnitureRecyclerView.visibility == View.VISIBLE) {
                furnitureRecyclerView.visibility = View.GONE
                toggleButton.setImageResource(R.drawable.ic_arrow_up)
            } else {
                furnitureRecyclerView.visibility = View.VISIBLE
                toggleButton.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        // 左上角小房子 → 回主選單
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 讀取倉庫已擁有的物品
        val prefs = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val owned = prefs.getStringSet("owned_items", emptySet()) ?: emptySet()

        if (owned.isEmpty()) {
            // 倉庫沒有東西 → 顯示提示文字
            furnitureRecyclerView.visibility = View.GONE
            emptyMessage.visibility = View.VISIBLE
            emptyMessage.text = "你還沒有家具，去商城逛逛吧"
            emptyMessage.textSize = 24f
            emptyMessage.setTextColor(android.graphics.Color.BLACK)
        } else {
            // 過濾家具清單，只顯示倉庫裡有的物品
            val filteredList = furnitureList.filter { owned.contains(it.name) }

            furnitureRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            furnitureRecyclerView.adapter = FurnitureAdapter(filteredList) { furniture ->
                addFurnitureToRoom(furniture, 100f, 100f)
            }

            emptyMessage.visibility = View.GONE

            // 載入已儲存的家具佈局
            loadFurnitureLayout(filteredList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.room_menu, menu)
        val saveItem = menu?.findItem(R.id.action_save)
        saveItem?.icon?.setTint(android.graphics.Color.BLACK)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveFurnitureLayout()
                Toast.makeText(this, "房間佈局已儲存！", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 新增家具到房間
    private fun addFurnitureToRoom(furniture: Furniture, posX: Float, posY: Float) {
        val furnitureView = ImageView(this).apply {
            setImageResource(furniture.drawableRes)
            tag = furniture.id.toString()

            if (furniture.name == "地毯") {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    300,
                    Gravity.BOTTOM
                )
            } else {
                layoutParams = FrameLayout.LayoutParams(200, 200)
                x = posX
                y = posY
            }
        }

        if (furniture.name != "地毯") {
            furnitureView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val parentWidth = roomLayout.width
                        val parentHeight = roomLayout.height

                        var newX = event.rawX - v.width / 2
                        var newY = event.rawY - v.height / 2

                        // 邊界檢查
                        if (newX < 0f) newX = 0f
                        if (newY < 0f) newY = 0f
                        if (newX + v.width > parentWidth) newX = (parentWidth - v.width).toFloat()
                        if (newY + v.height > parentHeight) newY = (parentHeight - v.height).toFloat()

                        v.x = newX
                        v.y = newY
                    }
                    MotionEvent.ACTION_UP -> {
                        saveFurniturePosition(furniture.id, v.x, v.y)
                    }
                }
                true
            }

            furnitureView.setOnLongClickListener {
                roomLayout.removeView(furnitureView)
                removeFurniturePosition(furniture.id)
                Toast.makeText(this, "${furniture.name} 已刪除", Toast.LENGTH_SHORT).show()
                true
            }
        }

        when (furniture.name) {
            "地毯" -> roomLayout.addView(furnitureView, 0)
            "畫作" -> roomLayout.addView(furnitureView)
            else -> roomLayout.addView(furnitureView, roomLayout.childCount - 1)
        }
    }

    private fun saveFurniturePosition(id: Int, x: Float, y: Float) {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putFloat("furniture_${id}_x", x)
            putFloat("furniture_${id}_y", y)
            apply()
        }
    }

    private fun removeFurniturePosition(id: Int) {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            remove("furniture_${id}_x")
            remove("furniture_${id}_y")
            apply()
        }
    }

    private fun saveFurnitureLayout() {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        for (furniture in furnitureList) {
            val view = roomLayout.findViewWithTag<ImageView>(furniture.id.toString())
            if (view != null) {
                editor.putFloat("furniture_${furniture.id}_x", view.x)
                editor.putFloat("furniture_${furniture.id}_y", view.y)
            }
        }
        editor.apply()
    }

    private fun loadFurnitureLayout(filteredList: List<Furniture>) {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        for (furniture in filteredList) {
            val x = prefs.getFloat("furniture_${furniture.id}_x", -1f)
            val y = prefs.getFloat("furniture_${furniture.id}_y", -1f)
            if (furniture.name == "地毯") {
                addFurnitureToRoom(furniture, 0f, 0f)
            } else if (x != -1f && y != -1f) {
                addFurnitureToRoom(furniture, x, y)
            }
        }
    }
}
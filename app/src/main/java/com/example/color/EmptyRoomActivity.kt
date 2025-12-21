package com.example.color

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.GestureDetector

class EmptyRoomActivity : AppCompatActivity() {

    private lateinit var roomLayout: FrameLayout
    private lateinit var furnitureRecyclerView: RecyclerView
    private lateinit var emptyMessage: TextView

    // 全部家具清單
    private val furnitureList = listOf(
        Furniture(1, "小沙發", R.drawable.sofa)
        // 其他家具可依需求加上
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
            furnitureRecyclerView.visibility = View.GONE
            emptyMessage.visibility = View.VISIBLE
            emptyMessage.text = "你還沒有家具，去商城逛逛吧"
            emptyMessage.textSize = 24f
            emptyMessage.setTextColor(android.graphics.Color.BLACK)
        } else {
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

    // 新增家具到房間（支援雙擊刪除 + 保留拖曳/縮放/旋轉）
    private fun addFurnitureToRoom(furniture: Furniture, posX: Float, posY: Float) {
        val uniqueTag = "${java.util.UUID.randomUUID()}_${furniture.id}"

        val furnitureView = FurnitureView(this).apply {
            setImageResource(furniture.drawableRes)
            tag = uniqueTag
            layoutParams = FrameLayout.LayoutParams(200, 200)
            x = posX
            y = posY
        }

        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: android.view.MotionEvent): Boolean {
                roomLayout.removeView(furnitureView)
                Toast.makeText(this@EmptyRoomActivity, "${furniture.name} 已刪除", Toast.LENGTH_SHORT).show()

                val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
                val set = prefs.getStringSet("furniture_instances", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                val updatedSet = set.filterNot { it.startsWith("$uniqueTag,") }.toMutableSet()
                prefs.edit().putStringSet("furniture_instances", updatedSet).apply()

                return true
            }
        })

        furnitureView.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)   // 雙擊刪除
            v.onTouchEvent(event)                 // 保留原本拖曳/縮放/旋轉
            true
        }

        roomLayout.addView(furnitureView)

        // 新增時立即存入紀錄
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        val set = prefs.getStringSet("furniture_instances", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val data = "$uniqueTag,${furniture.id},${furnitureView.x},${furnitureView.y},${furnitureView.scaleX},${furnitureView.rotation}"
        set.add(data)
        prefs.edit().putStringSet("furniture_instances", set).apply()
    }



    // 儲存整個房間佈局
    private fun saveFurnitureLayout() {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val set = mutableSetOf<String>()

        for (i in 0 until roomLayout.childCount) {
            val view = roomLayout.getChildAt(i)
            if (view is FurnitureView && view.tag != null) {
                val tagParts = view.tag.toString().split("_")
                val uniqueTag = tagParts[0]
                val id = tagParts[1].toInt()
                val data = "$uniqueTag,$id,${view.x},${view.y},${view.scaleX},${view.rotation}"
                set.add(data)
            }
        }

        editor.putStringSet("furniture_instances", set)
        editor.apply()
    }

    // 載入家具佈局
    private fun loadFurnitureLayout(filteredList: List<Furniture>) {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        roomLayout.removeAllViews()

        val set = prefs.getStringSet("furniture_instances", emptySet()) ?: emptySet()

        for (data in set) {
            val parts = data.split(",")
            if (parts.size == 6) {
                val uniqueTag = parts[0]
                val id = parts[1].toInt()
                val x = parts[2].toFloat()
                val y = parts[3].toFloat()
                val scale = parts[4].toFloat()
                val rotation = parts[5].toFloat()

                val furniture = filteredList.find { it.id == id }
                if (furniture != null) {
                    val furnitureView = FurnitureView(this).apply {
                        setImageResource(furniture.drawableRes)
                        tag = "${uniqueTag}_${id}"
                        layoutParams = FrameLayout.LayoutParams(200, 200)
                        this.x = x
                        this.y = y
                        this.scaleX = scale
                        this.scaleY = scale
                        this.rotation = rotation
                    }

                    val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
                        override fun onDoubleTap(e: android.view.MotionEvent): Boolean {
                            roomLayout.removeView(furnitureView)
                            Toast.makeText(this@EmptyRoomActivity, "${furniture.name} 已刪除", Toast.LENGTH_SHORT).show()

                            val set = prefs.getStringSet("furniture_instances", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                            val updatedSet = set.filterNot { it.startsWith("$uniqueTag,") }.toMutableSet()
                            prefs.edit().putStringSet("furniture_instances", updatedSet).apply()

                            return true
                        }
                    })

                    furnitureView.setOnTouchListener { v, event ->
                        gestureDetector.onTouchEvent(event)   // 雙擊刪除
                        v.onTouchEvent(event)                 // 保留原本拖曳/縮放/旋轉
                        true
                    }

                    roomLayout.addView(furnitureView)
                }
            }
        }
    }
}
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

    // 新增家具到房間
    private fun addFurnitureToRoom(furniture: Furniture, posX: Float, posY: Float) {
        val furnitureView = FurnitureView(this).apply {
            setImageResource(furniture.drawableRes)
            tag = furniture.id.toString()
            layoutParams = FrameLayout.LayoutParams(200, 200)
            x = posX
            y = posY
        }
        roomLayout.addView(furnitureView)
    }
    // 儲存家具位置 + 縮放 + 旋轉
    private fun saveFurniturePosition(id: Int, x: Float, y: Float, scale: Float, rotation: Float) {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // 每次新增一個家具就存成一個字串
        val furnitureData = "$id,$x,$y,$scale,$rotation"

        // 取出已存的集合，加入新的
        val set = prefs.getStringSet("furniture_instances", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        set.add(furnitureData)

        editor.putStringSet("furniture_instances", set)
        editor.apply()
    }

    private fun saveFurnitureLayout() {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val set = mutableSetOf<String>()

        // 遍歷房間裡所有家具
        for (i in 0 until roomLayout.childCount) {
            val view = roomLayout.getChildAt(i)
            if (view is FurnitureView && view.tag != null) {
                val id = view.tag.toString().toInt()
                val data = "$id,${view.x},${view.y},${view.scaleX},${view.rotation}"
                set.add(data)
            }
        }

        editor.putStringSet("furniture_instances", set)
        editor.apply()
    }

    private fun loadFurnitureLayout(filteredList: List<Furniture>) {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        val set = prefs.getStringSet("furniture_instances", emptySet()) ?: emptySet()

        // 只有有紀錄才生成家具
        for (data in set) {
            val parts = data.split(",")
            if (parts.size == 5) {
                val id = parts[0].toInt()
                val x = parts[1].toFloat()
                val y = parts[2].toFloat()
                val scale = parts[3].toFloat()
                val rotation = parts[4].toFloat()

                val furniture = filteredList.find { it.id == id }
                if (furniture != null) {
                    val furnitureView = FurnitureView(this).apply {
                        setImageResource(furniture.drawableRes)
                        tag = furniture.id.toString()
                        layoutParams = FrameLayout.LayoutParams(200, 200)
                        this.x = x
                        this.y = y
                        this.scaleX = scale
                        this.scaleY = scale
                        this.rotation = rotation
                    }
                    roomLayout.addView(furnitureView)
                }
            }
        }
    }
}
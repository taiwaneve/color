package com.example.color

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.color.Furniture
import com.example.color.FurnitureAdapter

class EmptyRoomActivity : AppCompatActivity() {

    private lateinit var roomLayout: FrameLayout
    private lateinit var furnitureRecyclerView: RecyclerView

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
        Furniture(10, "地毯", R.drawable.carpet) // 新增地毯*/
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_room)

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

        roomLayout = findViewById(R.id.roomLayout)
        furnitureRecyclerView = findViewById(R.id.furnitureRecyclerView)

        // 設定 RecyclerView
        furnitureRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        furnitureRecyclerView.adapter = FurnitureAdapter(furnitureList) { furniture ->
            addFurnitureToRoom(furniture, 100f, 100f)
        }

        // 讀取已儲存的家具佈局
        loadFurnitureLayout()
    }

    private fun addFurnitureToRoom(furniture: Furniture, posX: Float, posY: Float) {
        val furnitureView = ImageView(this).apply {
            setImageResource(furniture.drawableRes)

            // 地毯特殊邏輯：鋪在底部，不能拖曳或刪除
            if (furniture.name == "地毯") {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    300, // 地毯高度，可調整
                    Gravity.BOTTOM
                )
            } else {
                layoutParams = FrameLayout.LayoutParams(200, 200)
                x = posX
                y = posY
            }
        }

        if (furniture.name != "地毯") {
            // 支援拖曳移動
            furnitureView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        v.x = event.rawX - v.width / 2
                        v.y = event.rawY - v.height / 2
                    }
                    MotionEvent.ACTION_UP -> {
                        saveFurniturePosition(furniture.id, v.x, v.y)
                    }
                }
                true
            }

            // 長按刪除家具
            furnitureView.setOnLongClickListener {
                roomLayout.removeView(furnitureView)
                removeFurniturePosition(furniture.id)
                true
            }
        }

        // 層級控制：地毯最底層，畫作最上層，其他家具在中間
        when (furniture.name) {
            "地毯" -> roomLayout.addView(furnitureView, 0) // 最底層
            "畫作" -> roomLayout.addView(furnitureView) // 最上層 (預設最後加入)
            else -> roomLayout.addView(furnitureView, roomLayout.childCount - 1) // 中間
        }
    }

    // 儲存家具位置
    private fun saveFurniturePosition(id: Int, x: Float, y: Float) {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putFloat("furniture_${id}_x", x)
            putFloat("furniture_${id}_y", y)
            apply()
        }
    }

    // 移除家具位置
    private fun removeFurniturePosition(id: Int) {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            remove("furniture_${id}_x")
            remove("furniture_${id}_y")
            apply()
        }
    }

    // 讀取家具佈局
    private fun loadFurnitureLayout() {
        val prefs = getSharedPreferences("room_prefs", Context.MODE_PRIVATE)
        for (furniture in furnitureList) {
            val x = prefs.getFloat("furniture_${furniture.id}_x", -1f)
            val y = prefs.getFloat("furniture_${furniture.id}_y", -1f)
            if (furniture.name == "地毯") {
                addFurnitureToRoom(furniture, 0f, 0f) // 地毯固定鋪底
            } else if (x != -1f && y != -1f) {
                addFurnitureToRoom(furniture, x, y)
            }
        }
    }
}
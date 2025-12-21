package com.example.color

// 商店商品資料類別
data class ShopItem(
    val name: String,
    val price: Int,
    val category: String,
    val iconResId: Int // 新增：圖片資源 ID
)
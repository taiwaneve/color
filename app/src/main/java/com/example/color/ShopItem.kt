package com.example.color

// 商店商品資料類別
data class ShopItem(
    val name: String,    // 商品名稱
    val price: Int,      // 商品價格
    val category: String // 商品類別 (家具 / 玩具)
)
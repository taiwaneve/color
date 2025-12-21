package com.example.color

// 家具資料類別
data class Furniture(
    val id: Int,
    val name: String,
    val drawableRes: Int
)

// 家具清單（目前 10 個）
object FurnitureData {
    val furnitureList = listOf(
        Furniture(1, "小沙發", R.drawable.sofa),
        /*Furniture(2, "玩具熊", R.drawable.teddy_bear),
        Furniture(3, "書桌", R.drawable.desk),
        Furniture(4, "盆栽", R.drawable.plant),
        Furniture(5, "電視", R.drawable.tv),
        Furniture(6, "燈具", R.drawable.lamp),
        Furniture(7, "畫作", R.drawable.painting),
        Furniture(8, "床", R.drawable.bed),
        Furniture(9, "小汽車玩具", R.drawable.toy_car),
        Furniture(10, "地毯", R.drawable.carpet)*/
    )
}
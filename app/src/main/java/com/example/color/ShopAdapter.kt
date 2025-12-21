package com.example.color

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ShopAdapter(private val context: Context, private val items: List<ShopItem>) : BaseAdapter() {

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_shop, parent, false)

        val item = items[position]

        val icon = view.findViewById<ImageView>(R.id.itemIcon)
        val name = view.findViewById<TextView>(R.id.itemName)
        val price = view.findViewById<TextView>(R.id.itemPrice)
        val category = view.findViewById<TextView>(R.id.itemCategory)

        // 綁定資料
        icon.setImageResource(item.iconResId)
        name.text = item.name
        price.text = "價格：${item.price} 點"
        category.text = "類別：${item.category}"

        return view
    }
}
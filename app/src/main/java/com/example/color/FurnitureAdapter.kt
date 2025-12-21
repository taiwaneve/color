package com.example.color

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.color.R

class FurnitureAdapter(
    private val furnitureList: List<Furniture>,
    private val onItemClick: (Furniture) -> Unit
) : RecyclerView.Adapter<FurnitureAdapter.FurnitureViewHolder>() {

    inner class FurnitureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.furnitureIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FurnitureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_furniture, parent, false)
        return FurnitureViewHolder(view)
    }

    override fun onBindViewHolder(holder: FurnitureViewHolder, position: Int) {
        val furniture = furnitureList[position]
        holder.icon.setImageResource(furniture.drawableRes)
        holder.itemView.setOnClickListener { onItemClick(furniture) }
    }

    override fun getItemCount() = furnitureList.size
}
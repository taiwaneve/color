package com.example.color

import android.widget.TextView

class ColorDisplay(private val textView: TextView) {
    var score: Int = 0
        private set

    fun showColor(color: Int) {
        textView.setBackgroundColor(color)
    }

    fun addScore(points: Int = 10) {
        score += points
    }

    fun resetScore() {
        score = 0
        // 顯示回灰色背景，視覺上表示重置
        showColor(0xFFD3D3D3.toInt())
    }
    fun setScore(newScore: Int) {
        score = newScore
    }
}
package com.example.color

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import java.lang.StrictMath.toDegrees
import kotlin.math.atan2

class FurnitureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private var scaleFactor = 1.0f
    private var rotationDegrees = 0f
    private var lastX = 0f
    private var lastY = 0f

    private val scaleDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(0.5f, 3.0f) // 限制縮放範圍
                scaleX = scaleFactor
                scaleY = scaleFactor
                return true
            }
        })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1) {
                    // 單指拖曳
                    val dx = event.rawX - lastX
                    val dy = event.rawY - lastY
                    x += dx
                    y += dy
                    lastX = event.rawX
                    lastY = event.rawY
                } else if (event.pointerCount == 2) {
                    // 雙指旋轉
                    val dx = event.getX(1) - event.getX(0)
                    val dy = event.getY(1) - event.getY(0)
                    rotationDegrees = toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                    rotation = rotationDegrees
                }
            }
        }
        return true
    }
}
package com.example.color

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

class FurnitureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private var lastX = 0f
    private var lastY = 0f

    // 旋轉基準角度
    private var initialRotation = 0f
    private var startAngle = 0f

    // 縮放偵測器
    private val scaleDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                // 平滑縮放 + 邊界限制
                scaleX = (scaleX * scaleFactor).coerceIn(0.5f, 3.0f)
                scaleY = (scaleY * scaleFactor).coerceIn(0.5f, 3.0f)
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

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    // 記錄初始角度
                    val dx = event.getX(1) - event.getX(0)
                    val dy = event.getY(1) - event.getY(0)
                    startAngle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
                    initialRotation = rotation
                }
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
                    // 雙指旋轉（相對角度）
                    val dx = event.getX(1) - event.getX(0)
                    val dy = event.getY(1) - event.getY(0)
                    val currentAngle = atan2(dy.toDouble(), dx.toDouble()).toFloat()
                    rotation = initialRotation + Math.toDegrees((currentAngle - startAngle).toDouble()).toFloat()
                }
            }
        }
        return true
    }
}
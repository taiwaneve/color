package com.example.color

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var colorDisplay: ColorDisplay
    private lateinit var inputHandler: InputHandler

    private lateinit var statusText: TextView
    private lateinit var colorTextView: TextView
    private lateinit var btnStart: Button

    private lateinit var blockRed: View
    private lateinit var blockYellow: View
    private lateinit var blockBlue: View
    private lateinit var blockGreen: View

    private val colors = listOf(Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN)
    private val sequence = mutableListOf<Int>()
    private var level = 1
    private var isPlayingSequence = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 取得 UI 元件
        colorTextView = findViewById(R.id.colorTextView)
        statusText = findViewById(R.id.statusText)
        btnStart = findViewById(R.id.btnStart)

        blockRed = findViewById(R.id.blockRed)
        blockYellow = findViewById(R.id.blockYellow)
        blockBlue = findViewById(R.id.blockBlue)
        blockGreen = findViewById(R.id.blockGreen)

        // 初始化模組
        colorDisplay = ColorDisplay(colorTextView)
        inputHandler = InputHandler(sequence, ::onCorrect, ::onWrong)

        // 色塊點擊事件（加入閃爍效果）
        blockRed.setOnClickListener {
            if (isPlayingSequence) return@setOnClickListener
            flashBlock(blockRed)
            inputHandler.checkInput(Color.RED)
        }
        blockYellow.setOnClickListener {
            if (isPlayingSequence) return@setOnClickListener
            flashBlock(blockYellow)
            inputHandler.checkInput(Color.YELLOW)
        }
        blockBlue.setOnClickListener {
            if (isPlayingSequence) return@setOnClickListener
            flashBlock(blockBlue)
            inputHandler.checkInput(Color.BLUE)
        }
        blockGreen.setOnClickListener {
            if (isPlayingSequence) return@setOnClickListener
            flashBlock(blockGreen)
            inputHandler.checkInput(Color.GREEN)
        }

        btnStart.setOnClickListener { startGame() }

        setGameBlocksEnabled(false)
    }

    private fun setGameBlocksEnabled(enabled: Boolean) {
        blockRed.isEnabled = enabled
        blockYellow.isEnabled = enabled
        blockBlue.isEnabled = enabled
        blockGreen.isEnabled = enabled
    }

    private fun startGame() {
        sequence.clear()
        level = 1
        colorDisplay.resetScore()
        statusText.text = "遊戲開始！"
        btnStart.visibility = View.GONE // ✅ 開始後隱藏按鈕
        nextLevel()
    }

    private fun nextLevel() {
        sequence.add(colors[Random.nextInt(colors.size)])
        inputHandler.reset()
        playSequence()
    }

    private fun playSequence() {
        isPlayingSequence = true
        setGameBlocksEnabled(false)

        var delay = 0L
        val stepDuration = 800L
        val gapDuration = 200L

        sequence.forEachIndexed { index, color ->
            handler.postDelayed({
                colorDisplay.showColor(color)
                statusText.text = "第 $level 關：記住第 ${index + 1} 個顏色"
            }, delay)

            handler.postDelayed({
                colorDisplay.showColor(Color.LTGRAY)
            }, delay + stepDuration)

            delay += (stepDuration + gapDuration)
        }

        handler.postDelayed({
            isPlayingSequence = false
            setGameBlocksEnabled(true)
            statusText.text = "第 $level 關，請依序輸入顏色！"
        }, delay)
    }

    private fun onCorrect() {
        colorDisplay.addScore(10)
        statusText.text = "正確！分數：${colorDisplay.score}，進入下一關..."
        level++
        setGameBlocksEnabled(false)
        handler.postDelayed({ nextLevel() }, 600L)
    }

    private fun onWrong() {
        statusText.text = "答錯了！最終分數：${colorDisplay.score}\n再玩一次，按下開始遊戲！"
        setGameBlocksEnabled(false)
        btnStart.visibility = View.VISIBLE // ✅ 遊戲結束後顯示按鈕
        colorDisplay.showColor(Color.LTGRAY)
    }

    // 色塊閃爍效果
    private fun flashBlock(block: View) {
        block.alpha = 0.5f
        handler.postDelayed({
            block.alpha = 1f
        }, 200L)
    }
}
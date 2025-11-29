package com.example.color

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private lateinit var btnRed: Button
    private lateinit var btnYellow: Button
    private lateinit var btnBlue: Button
    private lateinit var btnGreen: Button

    private val colors = listOf(Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN)
    private val sequence = mutableListOf<Int>()
    private var level = 1
    private var isPlayingSequence = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // ✅ 使用 XML

        // 取得 UI 元件
        colorTextView = findViewById(R.id.colorTextView)
        statusText = findViewById(R.id.statusText)
        btnStart = findViewById(R.id.btnStart)
        btnRed = findViewById(R.id.btnRed)
        btnYellow = findViewById(R.id.btnYellow)
        btnBlue = findViewById(R.id.btnBlue)
        btnGreen = findViewById(R.id.btnGreen)

        // 初始化模組
        colorDisplay = ColorDisplay(colorTextView)
        inputHandler = InputHandler(sequence, ::onCorrect, ::onWrong)

        // 設定按鈕事件（修正 return 錯誤）
        btnRed.setOnClickListener {
            if (isPlayingSequence) return@setOnClickListener
            inputHandler.checkInput(Color.RED)
        }
        btnYellow.setOnClickListener {
            if (isPlayingSequence) return@setOnClickListener
            inputHandler.checkInput(Color.YELLOW)
        }
        btnBlue.setOnClickListener {
            if (isPlayingSequence) return@setOnClickListener
            inputHandler.checkInput(Color.BLUE)
        }
        btnGreen.setOnClickListener {
            if (isPlayingSequence) return@setOnClickListener
            inputHandler.checkInput(Color.GREEN)
        }

        btnStart.setOnClickListener { startGame() }

        setGameButtonsEnabled(false)
    }

    private fun setGameButtonsEnabled(enabled: Boolean) {
        btnRed.isEnabled = enabled
        btnYellow.isEnabled = enabled
        btnBlue.isEnabled = enabled
        btnGreen.isEnabled = enabled
    }

    private fun startGame() {
        sequence.clear()
        level = 1
        colorDisplay.resetScore()
        statusText.text = "遊戲開始！"
        btnStart.isEnabled = false
        nextLevel()
    }

    private fun nextLevel() {
        sequence.add(colors[Random.nextInt(colors.size)])
        inputHandler.reset()
        playSequence()
    }

    private fun playSequence() {
        isPlayingSequence = true
        setGameButtonsEnabled(false)

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
            setGameButtonsEnabled(true)
            statusText.text = "第 $level 關，請依序輸入顏色！"
        }, delay)
    }

    private fun onCorrect() {
        colorDisplay.addScore(10)
        statusText.text = "正確！分數：${colorDisplay.score}，進入下一關..."
        level++
        setGameButtonsEnabled(false)
        handler.postDelayed({ nextLevel() }, 600L)
    }

    private fun onWrong() {
        statusText.text = "答錯了！最終分數：${colorDisplay.score}"
        setGameButtonsEnabled(false)
        btnStart.isEnabled = true
        colorDisplay.showColor(Color.LTGRAY)
    }
}
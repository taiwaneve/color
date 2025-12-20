package com.example.color

import android.content.Context
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
    private lateinit var livesText: TextView

    private lateinit var blockRed: View
    private lateinit var blockYellow: View
    private lateinit var blockBlue: View
    private lateinit var blockGreen: View

    private val colors = listOf(Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN)
    private val sequence = mutableListOf<Int>()
    private var level = 1
    private var isPlayingSequence = false
    private val handler = Handler(Looper.getMainLooper())

    // 遊戲狀態
    private var questionCount = 0
    private var maxQuestions = 10
    private var lives = 1
    private var difficulty = "hard"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 接收難度參數（若未提供，預設困難）
        difficulty = intent.getStringExtra("difficulty") ?: "hard"
        applyDifficultyDefaults()

        // 取得 UI 元件
        colorTextView = findViewById(R.id.colorTextView)
        statusText = findViewById(R.id.statusText)
        btnStart = findViewById(R.id.btnStart)
        livesText = findViewById(R.id.livesText)

        blockRed = findViewById(R.id.blockRed)
        blockYellow = findViewById(R.id.blockYellow)
        blockBlue = findViewById(R.id.blockBlue)
        blockGreen = findViewById(R.id.blockGreen)

        // 初始化模組
        colorDisplay = ColorDisplay(colorTextView)
        inputHandler = InputHandler(sequence, ::onCorrect, ::onWrong)

        // 色塊點擊事件
        blockRed.setOnClickListener { if (!isPlayingSequence) { flashBlock(blockRed); inputHandler.checkInput(Color.RED) } }
        blockYellow.setOnClickListener { if (!isPlayingSequence) { flashBlock(blockYellow); inputHandler.checkInput(Color.YELLOW) } }
        blockBlue.setOnClickListener { if (!isPlayingSequence) { flashBlock(blockBlue); inputHandler.checkInput(Color.BLUE) } }
        blockGreen.setOnClickListener { if (!isPlayingSequence) { flashBlock(blockGreen); inputHandler.checkInput(Color.GREEN) } }

        btnStart.setOnClickListener { startGame() }

        setGameBlocksEnabled(false)
        btnStart.visibility = View.VISIBLE
        livesText.visibility = View.GONE
    }

    private fun applyDifficultyDefaults() {
        when (difficulty) {
            "easy" -> { maxQuestions = 5; lives = 5 }
            "normal" -> { maxQuestions = 7; lives = 3 }
            "hard" -> { maxQuestions = 10; lives = 1 }
        }
    }

    private fun setGameBlocksEnabled(enabled: Boolean) {
        blockRed.isEnabled = enabled
        blockYellow.isEnabled = enabled
        blockBlue.isEnabled = enabled
        blockGreen.isEnabled = enabled
    }

    private fun startGame() {
        applyDifficultyDefaults()

        sequence.clear()
        level = 1
        questionCount = 0
        colorDisplay.resetScore()
        statusText.text = "遊戲開始！（難度：$difficulty）"

        btnStart.visibility = View.GONE
        livesText.visibility = View.VISIBLE
        livesText.text = "生命值：$lives"

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

    private fun replayCurrentQuestion() {
        inputHandler.reset()
        playSequence()
    }

    private fun onCorrect() {
        questionCount++

        val points = when (difficulty) {
            "easy" -> 5
            "normal" -> if (questionCount in 1..3) 5 else 10
            "hard" -> when (questionCount) {
                in 1..3 -> 10
                in 4..6 -> 15
                in 7..10 -> 20
                else -> 10
            }
            else -> 5
        }

        colorDisplay.addScore(points)
        statusText.text = "正確！分數：${colorDisplay.score}"

        if (questionCount == maxQuestions) {
            colorDisplay.setScore(colorDisplay.score * 2)
            statusText.text = "完成一大關！分數翻倍：${colorDisplay.score}"
            saveScore(colorDisplay.score)
            handler.postDelayed({ finish() }, 2000L)
        } else {
            level++
            setGameBlocksEnabled(false)
            handler.postDelayed({ nextLevel() }, 600L)
        }
    }

    private fun onWrong() {
        lives--
        livesText.text = "生命值：$lives"
        statusText.text = "答錯了！扣除 1 點生命值（剩餘：$lives）"
        setGameBlocksEnabled(false)

        if (lives <= 0) {
            handler.postDelayed({
                statusText.text = "遊戲結束！最終分數：${colorDisplay.score}"
                saveScore(colorDisplay.score)
                colorDisplay.showColor(Color.LTGRAY)
                btnStart.visibility = View.VISIBLE
                livesText.visibility = View.GONE
                setGameBlocksEnabled(false)
            }, 1000L)
        } else {
            handler.postDelayed({
                statusText.text = "第 $level 關，請再試一次！"
                replayCurrentQuestion()
            }, 1000L)
        }
    }

    private fun saveScore(score: Int) {
        val prefs = getSharedPreferences("game_scores", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val history = prefs.getString("scores", "") ?: ""
        val newHistory = if (history.isEmpty()) "$score" else "$history,$score"
        editor.putString("scores", newHistory)
        editor.apply()
    }

    private fun flashBlock(block: View) {
        block.alpha = 0.5f
        handler.postDelayed({ block.alpha = 1f }, 200L)
    }
}
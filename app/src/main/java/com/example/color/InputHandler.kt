package com.example.color

class InputHandler(
    private val sequence: List<Int>,
    private val onCorrect: () -> Unit,
    private val onWrong: () -> Unit
) {
    private val playerInput = mutableListOf<Int>()

    fun checkInput(color: Int) {
        val currentIndex = playerInput.size

        // 還未輸入任何顏色但序列已空（理論上不會發生）
        if (sequence.isEmpty()) return

        // 逐步比對
        if (sequence[currentIndex] != color) {
            playerInput.clear()
            onWrong()
            return
        }

        playerInput.add(color)

        // 全部輸入完成且正確
        if (playerInput.size == sequence.size) {
            playerInput.clear()
            onCorrect()
        }
    }

    fun reset() {
        playerInput.clear()
    }
}
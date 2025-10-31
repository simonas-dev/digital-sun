package dev.simonas.digitalsun.core

class Stage {

    companion object {
        private const val verticalMargin = 8
        private const val verticalOffset = 6
        private const val horizontalMargin = 5
    }

    private val pixels: MutableList<Pixel> = create500Stage()

    fun getPixels(): List<Pixel> = pixels

    private fun MutableList<Pixel>.addRow(y: Int, fromX: Int, toX: Int) {
        for (x in fromX until toX) {
            add(Pixel(x, y))
        }
    }

    private fun create500Stage() = mutableListOf<Pixel>().apply {
        val centerX = 30 + 26
        // 24 pixels
        addRow(y = 0 * verticalMargin + verticalOffset, fromX = centerX - 15, toX = centerX +15)
        // 42 pixels
        addRow(y = 1 * verticalMargin + verticalOffset, fromX = centerX - 27, toX = centerX + 27)
        // 48 pixels
        addRow(y = 2 * verticalMargin + verticalOffset, fromX = centerX - 31, toX = centerX + 31)
        // 48 pixels
        addRow(y = 3 * verticalMargin + verticalOffset, fromX = centerX - 31, toX = centerX + 31)
        // 42 pixels
        addRow(y = 4 * verticalMargin + verticalOffset, fromX = centerX - 27, toX = centerX + 27)
        // 24 pixels
        addRow(y = 5 * verticalMargin + verticalOffset, fromX = centerX - 15, toX = centerX + 15)
    }

    private fun create370DStage() = mutableListOf<Pixel>().apply {
        val centerX = 30 + 26
        // 24 pixels
        addRow(y = 0 * verticalMargin + verticalOffset, fromX = centerX - 12, toX = centerX + 12)
        // 42 pixels
        addRow(y = 1 * verticalMargin + verticalOffset, fromX = centerX - 21, toX = centerX + 21)
        // 48 pixels
        addRow(y = 2 * verticalMargin + verticalOffset, fromX = centerX - 24, toX = centerX + 24)
        // 48 pixels
        addRow(y = 3 * verticalMargin + verticalOffset, fromX = centerX - 24, toX = centerX + 24)
        // 42 pixels
        addRow(y = 4 * verticalMargin + verticalOffset, fromX = centerX - 21, toX = centerX + 21)
        // 24 pixels
        addRow(y = 5 * verticalMargin + verticalOffset, fromX = centerX - 12, toX = centerX + 12)
    }
}

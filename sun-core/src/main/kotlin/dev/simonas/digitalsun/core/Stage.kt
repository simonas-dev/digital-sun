package dev.simonas.digitalsun.core

class Stage {

    companion object {
        private const val verticalMargin = 8
        private const val verticalOffset = 6
        private const val horizontalMargin = 5
    }

    private val pixels = mutableListOf<Pixel>().apply {
        addRow(y = 0 * verticalMargin + verticalOffset, fromX = 30 +  2 + horizontalMargin * 3, toX = 30 + 50 - horizontalMargin * 3)
        addRow(y = 1 * verticalMargin + verticalOffset, fromX = 30 +  2 + horizontalMargin * 1, toX = 30 + 50 - horizontalMargin * 1)
        addRow(y = 2 * verticalMargin + verticalOffset, fromX = 30 +  2, toX = 30 + 50)
        addRow(y = 3 * verticalMargin + verticalOffset, fromX = 30 +  2, toX = 30 + 50)
        addRow(y = 4 * verticalMargin + verticalOffset, fromX = 30 +  2 + horizontalMargin * 1, toX = 30 + 50 - horizontalMargin * 1)
        addRow(y = 5 * verticalMargin + verticalOffset, fromX = 30 +  2 + horizontalMargin * 3, toX = 30 + 50 - horizontalMargin * 3)
    }

    fun getPixels(): List<Pixel> = pixels

    private fun MutableList<Pixel>.addRow(y: Int, fromX: Int, toX: Int) {
        for (x in fromX until toX) {
            add(Pixel(x, y))
        }
    }
}

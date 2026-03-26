package dev.simonas.digitalsun.core

object Stages {

    private const val verticalMargin = 8
    private const val verticalOffset = 6
    private const val centerX = 30 + 26

    val V1 = createStage {
        // 30 pixels
        addRow(y = 0 * verticalMargin + verticalOffset, fromX = centerX - 15, toX = centerX + 15)
        // 54 pixels
        addRow(y = 1 * verticalMargin + verticalOffset, fromX = centerX - 27, toX = centerX + 27)
        // 62 pixels
        addRow(y = 2 * verticalMargin + verticalOffset, fromX = centerX - 31, toX = centerX + 31)
        // 62 pixels
        addRow(y = 3 * verticalMargin + verticalOffset, fromX = centerX - 31, toX = centerX + 31)
        // 54 pixels
        addRow(y = 4 * verticalMargin + verticalOffset, fromX = centerX - 27, toX = centerX + 27)
        // 30 pixels
        addRow(y = 5 * verticalMargin + verticalOffset, fromX = centerX - 15, toX = centerX + 15)
    }

    val V2 = createStage {
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

    val V3 = createStage {
        val v3VerticalMargin = 2
        // 14 pixels
        addRow(y = 0 * v3VerticalMargin + verticalOffset, fromX = centerX - 7, toX = centerX + 7)
        // 22 pixels
        addRow(y = 1 * v3VerticalMargin + verticalOffset, fromX = centerX - 11, toX = centerX + 11)
        // 28 pixels
        addRow(y = 2 * v3VerticalMargin + verticalOffset, fromX = centerX - 14, toX = centerX + 14)
        // 32 pixels
        addRow(y = 3 * v3VerticalMargin + verticalOffset, fromX = centerX - 16, toX = centerX + 16)
        // 36 pixels
        addRow(y = 4 * v3VerticalMargin + verticalOffset, fromX = centerX - 18, toX = centerX + 18)
        // 38 pixels
        addRow(y = 5 * v3VerticalMargin + verticalOffset, fromX = centerX - 19, toX = centerX + 19)
        // 40 pixels
        addRow(y = 6 * v3VerticalMargin + verticalOffset, fromX = centerX - 20, toX = centerX + 20)
        // 40 pixels
        addRow(y = 7 * v3VerticalMargin + verticalOffset, fromX = centerX - 20, toX = centerX + 20)
        // 42 pixels
        addRow(y = 8 * v3VerticalMargin + verticalOffset, fromX = centerX - 21, toX = centerX + 21)
        // 42 pixels
        addRow(y = 9 * v3VerticalMargin + verticalOffset, fromX = centerX - 21, toX = centerX + 21)
        // 42 pixels
        addRow(y = 10 * v3VerticalMargin + verticalOffset, fromX = centerX - 21, toX = centerX + 21)
        // 42 pixels
        addRow(y = 11 * v3VerticalMargin + verticalOffset, fromX = centerX - 21, toX = centerX + 21)
        // 42 pixels
        addRow(y = 12 * v3VerticalMargin + verticalOffset, fromX = centerX - 21, toX = centerX + 21)
        // 42 pixels
        addRow(y = 13 * v3VerticalMargin + verticalOffset, fromX = centerX - 21, toX = centerX + 21)
        // 40 pixels
        addRow(y = 14 * v3VerticalMargin + verticalOffset, fromX = centerX - 20, toX = centerX + 20)
        // 40 pixels
        addRow(y = 15 * v3VerticalMargin + verticalOffset, fromX = centerX - 20, toX = centerX + 20)
        // 38 pixels
        addRow(y = 16 * v3VerticalMargin + verticalOffset, fromX = centerX - 19, toX = centerX + 19)
        // 36 pixels
        addRow(y = 17 * v3VerticalMargin + verticalOffset, fromX = centerX - 18, toX = centerX + 18)
        // 32 pixels
        addRow(y = 18 * v3VerticalMargin + verticalOffset, fromX = centerX - 16, toX = centerX + 16)
        // 28 pixels
        addRow(y = 19 * v3VerticalMargin + verticalOffset, fromX = centerX - 14, toX = centerX + 14)
        // 22 pixels
        addRow(y = 20 * v3VerticalMargin + verticalOffset, fromX = centerX - 11, toX = centerX + 11)
        // 14 pixels
        addRow(y = 21 * v3VerticalMargin + verticalOffset, fromX = centerX - 7, toX = centerX + 7)
    }

    private val ALL = mapOf("v1" to V1, "v2" to V2, "v3" to V3)

    fun byName(name: String): Stage =
        ALL[name.lowercase()]
            ?: error("Unknown stage '$name'. Available: ${ALL.keys}")

    fun fromEnv(default: Stage = V3): Stage {
        val hw = System.getenv("HW") ?: return default
        return byName(hw)
    }

    private fun createStage(block: MutableList<Pixel>.() -> Unit): Stage {
        val pixels = mutableListOf<Pixel>()
        pixels.block()
        return Stage(pixels)
    }

private fun MutableList<Pixel>.addRow(y: Int, fromX: Int, toX: Int) {
        for (x in fromX until toX) {
            add(Pixel(x, y))
        }
    }
}

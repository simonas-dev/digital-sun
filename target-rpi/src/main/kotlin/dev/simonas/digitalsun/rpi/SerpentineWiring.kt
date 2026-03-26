package dev.simonas.digitalsun.rpi

import dev.simonas.digitalsun.core.Pixel

/**
 * Reorders pixels into serpentine data-chain order for physical LED strips.
 * Even rows (0, 2, 4...): left to right.
 * Odd rows (1, 3, 5...): right to left.
 */
fun List<Pixel>.serpentine(): List<Pixel> =
    groupBy { it.y }
        .toSortedMap()
        .flatMap { (rowIndex, row) ->
            val sorted = row.sortedBy { it.x }
            if (rowIndex % 2 == 0) sorted else sorted.reversed()
        }

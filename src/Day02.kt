import kotlin.math.abs
import kotlin.math.max

fun main() {
    fun part1(input: List<String>): Int {
        return input.count {
            val diff = it.split(" ").zipWithNext().map { i -> i.first.toInt() - i.second.toInt() }
            (diff.all { d -> d > 0 } or diff.all { d -> d < 0 }) and diff.none { d ->
                abs(d) > 3 || abs(
                    d
                ) == 0
            }
        }
    }

    fun part2(input: List<String>): Int {
        return input.count {
            val diff = it.split(" ").zipWithNext().map { i -> i.first.toInt() - i.second.toInt() }
            val groups = diff.mapIndexed { index, i -> index to i }
                .groupBy { v -> if (v.second > 0) 1 else if (v.second < 0) -1 else 0 }
            val positiveCount = groups[1]?.size ?: 0
            val negativeCount = groups[-1]?.size ?: 0
            if (positiveCount < diff.size - 1 && negativeCount < diff.size - 1) {
                return@count false
            }
            val v = if (positiveCount == diff.size - 1) {
                groups[-1]?.firstOrNull() ?: groups[0]?.first()
            } else if (negativeCount == diff.size - 1) {
                groups[1]?.firstOrNull() ?: groups[0]?.first()
            } else {
                null
            }
            val cleanDiff = if (v == null) diff else {
                val (i, value) = v
                val cals = ArrayList<Pair<Int, Int>>()
                if (i > 0) {
                    cals.add(i - 1 to diff[i] + diff[i - 1])
                }
                if (i < diff.size - 1) {
                    cals.add(i + 1 to diff[i] + diff[i + 1])
                }
                val newDiff = diff.toMutableList()
                val (selectedI, selectedV) = if (value > 0) {
                    cals.minBy { c -> c.second }
                } else if (value < 0) {
                    cals.maxBy { c -> c.second }
                } else {
                    cals.first()
                }
//                println("take off ${max(i , selectedI)} from $it")
                newDiff[selectedI] = selectedV
                when (i) {
                    0 -> {
                        newDiff.subList(1, diff.size - 1)
                    }

                    diff.size - 1 -> {
                        newDiff.subList(0, diff.size - 2)
                    }

                    else -> {
                        newDiff.subList(0, i) +
                                newDiff.subList(i + 1, newDiff.size - 1)
                    }
                }
            }
            cleanDiff.none { d -> abs(d) > 3 || abs(d) == 0 }
        }
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 5)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
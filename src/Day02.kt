import kotlin.math.abs

fun isSafeReportDiff(diff: List<Int>): Boolean {
    return (diff.all { d -> d > 0 } or diff.all { d -> d < 0 }) and diff.none { d ->
        abs(d) > 3 || abs(
            d
        ) == 0
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.count {
            val diff = it.split(" ").zipWithNext().map { i -> i.first.toInt() - i.second.toInt() }
            isSafeReportDiff(diff)
        }
    }

    fun part2(input: List<String>): Int {
        return input.count {
            val diff = it.split(" ").map { i -> i.toInt() }
            diff.indices.any { index ->
                val restoredDiff =
                    diff.toMutableList().also { ls -> ls.removeAt(index) }.zipWithNext().map { i -> i.first - i.second }
                isSafeReportDiff(restoredDiff)
            }
        }
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
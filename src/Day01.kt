import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        return input.map { s -> s.split("   ").mapIndexed { index, v -> index to v } }.flatten()
            .groupBy { it.first }.values.map { it.map { it.second }.sorted() }
            .map { it.mapIndexed { i, v -> i to v } }
            .flatten()
            .groupBy { it.first }
            .values
            .sumOf { abs(it[0].second.toInt() - it[1].second.toInt()) }
    }

    fun part2(input: List<String>): Int {
        val cache = HashMap<String, Int>()
        val groups =
            input.map { s -> s.split("   ").mapIndexed { index, v -> index to v } }.flatten()
                .groupBy { it.first }.values.map { it.map { it.second } }
        return groups[0].sumOf { g ->
            val v = cache[g]
            if (v != null) {
                v
            } else {
                val count = g.toInt() * groups[1].count { it == g }
                cache[g] = count
                count
            }
        }
    }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

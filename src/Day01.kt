import kotlin.math.abs

fun parseGroups(s: List<String>): List<List<Int>> {
    val (l, r) = s.map {
        val v = it.split("   ")
        v[0].toInt() to v[1].toInt()
    }.unzip()
    return listOf(l, r)
}


fun main() {
    fun part1(input: List<String>): Int {
        val groups = parseGroups(input).map { v -> v.sorted() }
        return groups[0].zip(groups[1]).sumOf { abs(it.first - it.second) }
    }

    fun part2(input: List<String>): Int {
        val groups = parseGroups(input)
        val cache = HashMap<Int, Int>()
        return groups[0].sumOf { g ->
            val v = cache[g]
            if (v != null) {
                v
            } else {
                val count = g * groups[1].count { it == g }
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

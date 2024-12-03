fun main() {
    fun part1(input: List<String>): Int {
        return Regex("""mul\((\d+),(\d+)\)""").findAll(input.joinToString("") { it }).sumOf {
            val p = it.groupValues.zipWithNext().last()
            p.first.toInt() * p.second.toInt()
        }
    }

    fun part2(input: List<String>): Int {
        val str = input.joinToString("") { it }
        val includingRanges =
            Regex("""do\(""").findAll(str).map { r -> r.range }.sortedByDescending { i -> i.first }
        val excludingRanges = Regex("""don't\(""").findAll(str).map { r -> r.range }
            .sortedByDescending { i -> i.first }
        return Regex("""mul\((\d+),(\d+)\)""").findAll(str).filter { r ->
            val range = r.range
            val r1 = includingRanges.firstOrNull { it.first < range.first }
            val r2 = excludingRanges.firstOrNull { it.first < range.first }
            r1 != null && r2 != null && r1.first > r2.first || r2 == null
        }.sumOf { it.groupValues.zipWithNext().last().run { first.toInt() * second.toInt() } }

    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 161)
    check(part2(testInput) == 48)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

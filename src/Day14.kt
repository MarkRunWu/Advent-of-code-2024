fun main() {

    fun part1(input: List<String>): Int {
        return input.map {
            val (pos, vel) = it.split(" ").map {
                val r = Regex("""(\d+),(\d+)""").find(it).groups
                r[1].value.toInt() to r[2].value.toInt()
            }
        }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1930)
    check(part2(testInput) == 1206)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
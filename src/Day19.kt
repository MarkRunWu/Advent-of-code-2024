fun String.isPossible(colors: Set<String>, possibles: MutableMap<String, Boolean>): Boolean {
    if (possibles.contains(this)) {
        return possibles[this]!!
    }
    val possible = colors.asSequence().filter { startsWith(it) }.any {
        if (it.length == length) true else substring(it.length).isPossible(colors, possibles)}
    possibles[this] = possible
    return possible
}

fun String.possibility(colors: Set<String>, possibles: MutableMap<String, Boolean>): Int {
    if (possibles.contains(this)) {
        return possibles[this]!!
    }
    val possible = colors.asSequence().filter { startsWith(it) }.any {
        if (it.length == length) true else substring(it.length).isPossible(colors, possibles)}
    possibles[this] = possible
    return possible
}

fun main() {
    fun part1(input: List<String>): Int {
        val colors = input.first().split(", ").toSet()
        val designs = input.subList(2, input.size)
        return designs.count {
            println(it)
            it.isPossible(colors, HashMap())
        }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day19_test")
    check(part1(testInput).also { println(it) } == 6)
    check(part2(testInput) == 0)

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}

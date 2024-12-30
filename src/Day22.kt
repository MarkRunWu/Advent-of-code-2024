import kotlin.math.roundToLong

fun Long.next(): Long {
    val step1 = (this * 64L).xor(this) % 16777216L
    val step2 = (step1 / 32L).xor(step1) % 16777216L
    return (step2 * 2048L).xor(step2) % 16777216L
}

fun main() {
    fun part1(input: List<String>): Long {
        return input.map {
            it.toLong()
        }.sumOf { num ->
            var current = num
            repeat(2000) {
                current = current.next()
            }
            current
        }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day22_test")
    check(part1(testInput).also { println(it) } == 37327623L)
    check(part2(testInput) == 0)

    val input = readInput("Day22")
    part1(input).println()
    part2(input).println()
}

import kotlin.math.roundToLong

fun Long.next(): Long {
    val step1 = (this * 64L).xor(this) % 16777216L
    val step2 = (step1 / 32L).xor(step1) % 16777216L
    return (step2 * 2048L).xor(step2) % 16777216L
}

data class Buyer(
    val prices: List<Int>,
) {
    private val diff = prices.zipWithNext { l, r -> r - l }
    val patterns = diff.run {
        val r = HashMap<List<Int>, Int>()
        for (i in 0..size - 4) {
            val pattern = diff.subList(i, i + 4)
            r.putIfAbsent(pattern, prices[i + 4])
        }
        r
    }
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
        val buyers = input.map {
            it.toLong()
        }.map { num ->
            val r = ArrayList<Int>()
            var current = num
            repeat(2000) {
                val price = (current % 10).toInt()
                r.add(price)
                current = current.next()
            }
            Buyer(prices = r)
        }
        return buyers.flatMap { it.patterns.entries }.groupBy { it.key }.mapValues { it.value.sumOf { v -> v.value } }
            .maxOf { it.value }
    }

    check(part1(listOf("1", "10", "100", "2024")).also { println(it) } == 37327623L)
    val testInput = readInput("Day22_test")
    check(part2(testInput).also { println(it) } == 23)

    val input = readInput("Day22")
    part1(input).println()
    part2(input).println()
}

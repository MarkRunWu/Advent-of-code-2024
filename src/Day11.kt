fun transformByBlinkingRule(stone: String): List<String> {
    return if (stone == "0") {
        listOf("1")
    } else if (stone.length % 2 == 0) {
        listOf(
            stone.substring(0..<stone.length / 2).toLong().toString(),
            stone.substring(stone.length / 2..<stone.length).toLong().toString()
        )
    } else {
        listOf((stone.toLong() * 2024L).toString())
    }
}

fun applyBlinkingRule(initialStones: List<String>): List<String> {
    return initialStones.flatMap {
        transformByBlinkingRule(it)
    }
}

fun getBlinkingRuleCount(stone: String, cache: HashMap<Int, MutableMap<String, Long>>, iter: Int): Long {
    if (iter == 0) {
        return 1L
    }
    return cache.getOrPut(iter) { HashMap() }.getOrPut(stone) {
        if (stone == "0") {
            getBlinkingRuleCount("1", cache, iter - 1)
        } else if (stone.length % 2 == 0) {
            getBlinkingRuleCount(stone.substring(0..<stone.length / 2).toLong().toString(), cache, iter - 1) +
                    getBlinkingRuleCount(
                        stone.substring(stone.length / 2..<stone.length).toLong().toString(),
                        cache,
                        iter - 1
                    )
        } else {
            getBlinkingRuleCount((stone.toLong() * 2024L).toString(), cache, iter - 1)
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        return input.map { it.split(" ") }.map {
            var current = it
            repeat(30) {
                current = applyBlinkingRule(current)
            }
            current
        }.sumOf { it.count() }
    }

    fun part2(input: List<String>): Long {
        return input.map { it.split(" ") }.sumOf { initial ->
            initial.sumOf { getBlinkingRuleCount(it, HashMap(), 75) }
        }
    }

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 55312)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}
fun main() {
    fun String.isLock(): Boolean {
        return startsWith("#####")
    }

    fun String.getPins(): List<Int> {
        return split(" ").flatMap {
            it.mapIndexed { i, c ->
                i to c
            }
        }.groupBy({ it.first }) { it.second }.map {
            it.value.count { c -> c == '#' } - 1
        }
    }

    fun parseKeyLocks(input: List<String>): Pair<List<List<Int>>, List<List<Int>>> {
        val sb = StringBuilder()
        val keys = ArrayList<List<Int>>()
        val locks = ArrayList<List<Int>>()
        val blocks = ArrayList<String>()
        for (r in input) {
            if (r == "") {
                blocks.add(sb.toString())
                sb.clear()
            } else {
                sb.append(r)
                sb.append(" ")
            }
        }
        blocks.add(sb.toString())
        for (block in blocks) {
            val kind = if (block.isLock()) {
                locks
            } else {
                keys
            }
            kind.add(block.getPins())
        }
        return keys to locks
    }

    fun day1(input: List<String>): Int {
        val (keys, locks) = parseKeyLocks(input)
        var count = 0
        for (k in keys) {
            for (l in locks) {
                if (k.zip(l).all { (l, r) -> l + r <= 5 }) {
                    count++
                }
            }
        }
        return count
    }

    fun day2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day25_test")

    check(day1(testInput) == 3)

    val input = readInput("Day25")
    day1(input).println()
    day2(input).println()
}
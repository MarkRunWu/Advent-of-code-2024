fun hasWordInDir(
    m: List<String>,
    start: Pair<Int, Int>,
    dir: Pair<Int, Int>,
    word: String
): Boolean {
    val (x, y) = start
    val (offsetX, offsetY) = dir
    for (i in word.indices) {
        val newX = x + i * offsetX
        val newY = y + i * offsetY
        if (newX !in m.indices || newY !in m[0].indices || m[newX][newY] != word[i]) {
            return false
        }
    }
    return true
}

fun findWordCount(m: List<String>, start: Pair<Int, Int>, word: String): Int {
    var count = 0
    for (i in -1..1) {
        for (j in -1..1) {
            if ((i != 0 || j != 0) && hasWordInDir(m, start, i to j, word)) {
                count++
            }
        }
    }
    return count
}

fun hasXShapMaxWordAt(m: List<String>, start: Pair<Int, Int>): Boolean {
    val (x, y) = start
    if (x !in 1..<m.size - 1 || y !in 1..<m[0].length - 1 ||
        m[x][y] != 'A'
    ) {
        return false
    }
    return listOf(
        listOf(-1 to 1, 1 to -1),
        listOf(-1 to -1, 1 to 1)
    ).map {
        it.joinToString("") { (offsetX, offsetY) ->
            m[x + offsetX][y + offsetY].toString()
        }
    }.count { it == "MS" || it == "SM" } == 2
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.indices.sumOf { i ->
            input[i].indices.sumOf { j ->
                findWordCount(input, i to j, "XMAS")
            }
        }
    }

    fun part2(input: List<String>): Int {
        return input.indices.sumOf { i ->
            input[i].indices.count { j ->
                hasXShapMaxWordAt(
                    input,
                    i to j
                )
            }
        }
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 9)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

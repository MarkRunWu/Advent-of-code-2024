fun minBFS(map: List<String>, start: Pair<Int, Int>, end: Pair<Int, Int>): Int {
    var picoSecs = 0
    val queue = arrayListOf(start)
    val visited = HashSet<Pair<Int, Int>>()
    while (queue.isNotEmpty()) {
        if (queue.contains(end)) {
            return picoSecs
        }
        val nextQueue = ArrayList<Pair<Int, Int>>()
        for (pos in queue) {
            visited.add(pos)
            for ((posX, posY) in Dir.entries.map {
                    pos + it.value
            }) {
                val nextPos = posX to posY
                if (posX in map[0].indices && posY in map.indices && map[posY][posX] != '#' && !visited.contains(nextPos)) {
                    nextQueue.add(nextPos)
                }
            }
        }
        queue.clear()
        queue.addAll(nextQueue.toSet())
        picoSecs++
    }
    return picoSecs
}

fun List<String>.getStartPosOrNone(): Pair<Int, Int>? {
    for (i in indices) {
        for (j in this[i].indices) {
            if (this[i][j] == 'S') {
                return j to i
            }
        }
    }
    return null
}

fun List<String>.getEndPosOrNone(): Pair<Int, Int>? {
    for (i in indices) {
        for (j in this[i].indices) {
            if (this[i][j] == 'E') {
                return j to i
            }
        }
    }
    return null
}


fun List<String>.getInnerWalls(): List<Pair<Int, Int>> {
    val wallPositions = ArrayList<Pair<Int, Int>>()
    for (i in 1..<size - 1) {
        for (j in 1..<this[0].length - 1) {
            if (this[i][j] == '#') {
                wallPositions.add(j to i)
            }
        }
    }
    return wallPositions
}

fun main() {
    fun part1(input: List<String>, minSavingPicoSec: Int): Int {
        val start = input.getStartPosOrNone() ?: return -1
        val end = input.getEndPosOrNone() ?: return -1
        val walls = input.getInnerWalls()
        val before = minBFS(input, start, end)
        val savingCostCount = HashMap<Int, Int>()
        for (wPos in walls) {
            val newMap = input.map { it.toMutableList() }.toMutableList()
            val (x, y) = wPos
            newMap[y][x] = '.'
            val after = minBFS(newMap.map { it.joinToString("") }, start, end)
            val diff = before - after
            if (diff > 0) {
                savingCostCount[diff] = savingCostCount.getOrPut(diff) { 0 } + 1
        }
        }
        return savingCostCount.also { println(it) }.filter { it.key >= minSavingPicoSec }.values.sum()
    }

    fun part2(input: List<String>): Long {
        return 0L
    }

    val testInput = readInput("Day20_test")
    check(part1(testInput, 64).also { println(it) } == 1)
    check(part2(testInput) == 0L)

    val input = readInput("Day20")
    part1(input, 100).println()
    part2(input).println()
}

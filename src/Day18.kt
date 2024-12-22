fun parseBytePositions(input: List<String>): List<Pair<Int, Int>> {
    return input.map { it.split(",") }.map { (f, s) -> f.toInt() to s.toInt() }
}

fun parseMemoryMap(
    bytePositions: List<Pair<Int, Int>>,
    size: Pair<Int, Int>,
    failingByteSize: Int
): List<String> {
    val brokenBytes = bytePositions.take(failingByteSize).toSet()
    val map = ArrayList<String>()
    val (w, h) = size
    for (i in 0..<h) {
        val l = ArrayList<Char>()
        for (j in 0..<w) {
            l.add(
                if (brokenBytes.contains(j to i)) {
                    '#'
                } else {
                    '.'
                }
            )
        }
        map.add(l.joinToString(""))
    }
    return map
}

fun bfs(map: List<String>): Int {
    val queue = arrayListOf(0 to 0)
    val goal = map.size - 1 to map.size - 1
    var step = 0
    val visited = HashSet<Pair<Int, Int>>()
    visited.addAll(queue)
    while (queue.isNotEmpty()) {
        if (queue.contains(goal)) {
            return step
        }
        val nextPts = ArrayList<Pair<Int, Int>>()
        for (pt in queue) {
            for (dir in Dir.entries) {
                val nextPt = pt + dir.value
                if (nextPt.first in map.indices && nextPt.second in map.indices &&
                    map[nextPt.second][nextPt.first] == '.' &&
                    !visited.contains(nextPt)
                ) {
                    visited.add(nextPt)
                    nextPts.add(nextPt)
                }
            }
        }
        step++
        queue.clear()
        queue.addAll(nextPts)
    }
    return -1
}

fun main() {
    fun part1(input: List<String>, failingCount: Int, size: Pair<Int, Int>): Int {
        val bytePositions = parseBytePositions(input)
        val map = parseMemoryMap(
            bytePositions,
            size,
            failingCount
        ).also { println(it.joinToString("\n")) }
        return bfs(map)
    }

    fun part2(input: List<String>, initialFailingCount: Int, size: Pair<Int, Int>): String {
        val bytePositions = parseBytePositions(input)
        var failingByteSize = initialFailingCount
        while (failingByteSize < input.size) {
            val map = parseMemoryMap(bytePositions, size, failingByteSize)
            if (bfs(map) == -1) {
                val (x, y) = bytePositions[failingByteSize - 1]
                return "$x,$y"
            }
            failingByteSize++
        }
        return ""
    }

    val testInput = readInput("Day18_test")
    check(part1(testInput, 12, 7 to 7).also { println(it) } == 22)
    check(part2(testInput, 12, 7 to 7).also { println(it) } == "6,1")

    val input = readInput("Day18")
    part1(input, 1024, 71 to 71).println()
    part2(input, 1024, 71 to 71).println()
}

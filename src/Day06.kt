import kotlin.math.abs
import kotlin.math.max

fun findObstacles(input: List<String>): List<Pair<Int, Int>> {
    return input.mapIndexed { i, s ->
        s.mapIndexed { j, v ->
            if (v == '#') {
                j to i
            } else {
                null
            }
        }.filterNotNull()
    }.flatten()
}

fun findOrigin(input: List<String>): Pair<Int, Int> {
    return input.mapIndexed { i, s ->
        val j = s.indexOf('^')
        if (j >= 0) {
            j to i
        } else {
            null
        }
    }.filterNotNull().first()
}

fun getGuardMap(input: List<String>): List<List<Char>> {
    val obstacles = findObstacles(input)
    val origin = findOrigin(input)
    var start = origin
    val dirs = listOf(
        0 to -1, // upward
        1 to 0, // right
        0 to 1, // downward
        -1 to 0 // left
    )
    val mutableMap = input.map { it.toMutableList() }.toMutableList()
    var dirIndex = 0
    var (dx, dy) = dirs[dirIndex]
    do {
        val (x, y) = start
        mutableMap[y][x] = when (mutableMap[y][x]) {
            'X' -> 'Y'
            else -> 'X'
        }
        val obstacle = obstacles.firstOrNull { (px, py) ->
            px == x + dx && py == y + dy
        }
        if (obstacle != null) {
            dirIndex = (dirIndex + 1) % dirs.size
            dx = dirs[dirIndex].first
            dy = dirs[dirIndex].second
        }
        start = x + dx to y + dy
    } while (start.first >= 0 && start.second >= 0 && start.first < input[0].length && start.second < input.size)
    return mutableMap
}

fun main() {
    fun part1(input: List<String>): Int {
        return getGuardMap(input).sumOf { it.count { s -> s == 'X' || s == 'Y' } }
    }

    fun part2(input: List<String>): Int {
        val origin = findOrigin(input)
        val obstacles = findObstacles(input)
        val guardMap = getGuardMap(input).map { it.toMutableList() }.toMutableList()
        val dirs = listOf(
            0 to -1, // upward
            1 to 0, // right
            0 to 1, // downward
            -1 to 0 // left
        )
        var dirIndex = 0
        var start = origin
        var (dx, dy) = dirs[dirIndex]
        var rotateCount = 0
        do {
            val (x, y) = start
            if (x + dx in input.indices && y + dy in input.indices && rotateCount >= 3) {
                val (nextDX, nextDY) = dirs[(dirIndex + 1) % dirs.size]
                if (obstacles.any {
                        val (diffX, diffY) = it.first - x to it.second - y
                        diffX / abs(max(1, diffX)) == nextDX && diffY / abs(max(1, diffY)) == nextDY
                    }) {
                    guardMap[y + dy][x + dx] = 'O'
                }
            }
            val obstacle = obstacles.firstOrNull { (px, py) ->
                px == x + dx && py == y + dy
            }
            if (obstacle != null) {
                dirIndex = (dirIndex + 1) % dirs.size
                rotateCount++
                dx = dirs[dirIndex].first
                dy = dirs[dirIndex].second
            }
            start = x + dx to y + dy
        } while (start.first >= 0 && start.second >= 0 && start.first < input[0].length && start.second < input.size)
        return guardMap.also { r -> println(r.joinToString("\n")) }
            .sumOf { it.count { s -> s == 'O' } }
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}

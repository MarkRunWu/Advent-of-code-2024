import kotlin.math.abs
import kotlin.math.sign

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

fun Pair<Int, Int>.distanceBetween(other: Pair<Int, Int>): Int {
    return abs(first - other.first) + abs(second - other.second)
}

fun Pair<Int, Int>.isOnThePath(origin: Pair<Int, Int>, dir: Pair<Int, Int>): Boolean {
    return (first - origin.first).sign == dir.first && (second - origin.second).sign == dir.second
}

fun Pair<Int, Int>.minus(dir: Pair<Int, Int>): Pair<Int, Int> {
    return first - dir.first to second - dir.second
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
            '^' -> '^'
            'X' -> 'Y'
            else -> when(dirs[dirIndex]) {
                1 to 0 -> '→'
                -1 to 0 -> '←'
                0 to -1 -> '↑'
                0 to 1 -> '↓'
                else -> '*'
            }
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

fun canMakeALoop(obstacles: List<Pair<Int, Int>>,
                 virtualObstacle: Pair<Int, Int>,
                 dirs: List<Pair<Int, Int>>, fromDirIndex: Int, from: Pair<Int, Int>): List<Pair<Int,Int>>{
    val visited = HashMap<Int, HashSet<Pair<Int, Int>>>().also {
        for (i in dirs.indices) {
            it.putIfAbsent(i, HashSet())
        }
    }
    visited[fromDirIndex]!!.add(virtualObstacle)
    var start = from
    val obstaclesOptions = obstacles + virtualObstacle
    var currentDirIndex = (fromDirIndex + 1) % dirs.size
    var dir = dirs[currentDirIndex]
    val selectedObstacles = ArrayList<Pair<Int, Int>>()
    selectedObstacles.add(virtualObstacle)
    while (true) {
        val ob = obstaclesOptions.filter {
            it.isOnThePath(start, dir)
        }.minByOrNull { it.distanceBetween(start) } ?: return emptyList()
        if (visited[currentDirIndex]!!.contains(ob)) {
            println("$ob $selectedObstacles")
            return selectedObstacles.toList()
        }
        start = ob.minus(dir)
        selectedObstacles.add(ob)
        visited[currentDirIndex]!!.add(ob)
        currentDirIndex = (currentDirIndex + 1) % dirs.size
        dir = dirs[currentDirIndex]
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return getGuardMap(input).sumOf { it.count { s -> s == 'X' || s == 'Y' || s == '^' } }
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
        var count = 0
        do {
            val (x, y) = start
            val obstacle = obstacles.firstOrNull{ (px, py) ->
                px == x + dx && py == y + dy
            }
            if (obstacle != null) {
                dirIndex = (dirIndex + 1) % dirs.size
                dx = dirs[dirIndex].first
                dy = dirs[dirIndex].second
            } else {
                val virtualObstacle = x + dx to y + dy
                if (virtualObstacle.first in input[0].indices && virtualObstacle.second in input.indices) {
                    val hits = canMakeALoop(obstacles, virtualObstacle, dirs, dirIndex, x to y)
                    if (hits.isNotEmpty()) {
                        val tmpMap = input.map { it.map { it.toString() }.toMutableList() }.toMutableList()
                        for(i in hits.indices) {
                            val (hx, hy) = hits[i]
                            tmpMap[hy][hx] += "[$i]"
                        }
                        println(tmpMap.joinToString("\n"))
                        println()
                        guardMap[virtualObstacle.second][virtualObstacle.first] = 'O'
                        count++
                    }
                }
            }
            start = x + dx to y + dy
        } while (start.first >= 0 && start.second >= 0 && start.first < input[0].length && start.second < input.size)
        println(count)
        return guardMap.also { r -> println(r.joinToString("\n")) }
            .sumOf { it.count { s -> s == 'O' } }
    }

    val testInput = readInput("Day06_test")
//    check(part1(testInput) == 41)
//    check(part2(testInput) == 6)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}

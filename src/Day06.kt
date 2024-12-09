import kotlin.math.abs
import kotlin.math.sign

enum class Dir {
    UP, RIGHT, DOWN, LEFT;

    companion object {
        val symbols = Dir.entries.map { it.symbol }
    }

    val value: Pair<Int, Int> get() {
        return when(this) {
                UP -> 0 to -1 // upward
                RIGHT -> 1 to 0 // right
                DOWN -> 0 to 1 // downward
                LEFT -> -1 to 0 // left
        }
    }

    val symbol: Char get() {
        return when(this) {
            RIGHT -> '→'
            LEFT -> '←'
            UP -> '↑'
            DOWN -> '↓'
        }
    }

    fun rotated(): Dir {
        return when(this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }
}

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
    val mutableMap = input.map { it.toMutableList() }.toMutableList()
    var dir = Dir.UP
    var (dx, dy) = dir.value
    do {
        val (x, y) = start
        mutableMap[y][x] = when (mutableMap[y][x]) {
            '^' -> '^'
            else -> dir.symbol
        }
        val obstacle = obstacles.firstOrNull { (px, py) ->
            px == x + dx && py == y + dy
        }
        if (obstacle != null) {
            dir = dir.rotated()
            dx = dir.value.first
            dy = dir.value.second
        } else {
            start = x + dx to y + dy
        }
    } while (start.first >= 0 && start.second >= 0 && start.first < input[0].length && start.second < input.size)
    return mutableMap
}

fun canMakeALoop(obstacles: List<Pair<Int, Int>>,
                 virtualObstacle: Pair<Int, Int>,
                 dir: Dir,from: Pair<Int, Int>): List<Pair<Int,Int>>{
    val visited = HashMap<Dir, HashSet<Pair<Int, Int>>>().also {
        for (i in Dir.entries) {
            it.putIfAbsent(i, HashSet())
        }
    }
    visited[dir]!!.add(virtualObstacle)
    var start = from
    val obstaclesOptions = obstacles + virtualObstacle
    var currentDir = dir.rotated()
    val selectedObstacles = ArrayList<Pair<Int, Int>>()
    selectedObstacles.add(virtualObstacle)
    while (true) {
        val ob = obstaclesOptions.filter {
            it.isOnThePath(start, currentDir.value)
        }.minByOrNull { it.distanceBetween(start) } ?: return emptyList()
        if (visited[currentDir]!!.contains(ob)) {
            return selectedObstacles.toList()
        }
        start = ob.minus(currentDir.value)
        selectedObstacles.add(ob)
        visited[currentDir]!!.add(ob)
        currentDir = currentDir.rotated()
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return getGuardMap(input).sumOf { it.count { s -> Dir.symbols.contains(s) || s == '^' } }
    }

    fun part2(input: List<String>): Int {
        val origin = findOrigin(input)
        val obstacles = findObstacles(input)
        val guardMap = getGuardMap(input).map { it.toMutableList() }.toMutableList()
        var start = origin
        var dir = Dir.UP
        var count = 0
        var (dx, dy) = dir.value
        do {
            val (x, y) = start
            val obstacle = obstacles.firstOrNull{ (px, py) ->
                px == x + dx && py == y + dy
            }
            if (obstacle != null) {
                dir = dir.rotated()
                dx = dir.value.first
                dy = dir.value.second
            } else {
                val virtualObstacle = x + dx to y + dy
                if (virtualObstacle.first in input[0].indices && virtualObstacle.second in input.indices) {
                    val hits = canMakeALoop(obstacles, virtualObstacle, dir, x to y)
                    if (hits.isNotEmpty()) {
                        val tmpMap = input.map { it.map { it.toString() }.toMutableList() }.toMutableList()
                        for(i in hits.indices) {
                            val (hx, hy) = hits[i]
                            tmpMap[hy][hx] += "[$i]"
                        }
                        guardMap[virtualObstacle.second][virtualObstacle.first] = 'O'
                        count++
                    }
                }
                start = x + dx to y + dy
            }
        } while (start.first >= 0 && start.second >= 0 && start.first < input[0].length && start.second < input.size)
        println(count)
        return guardMap.sumOf { it.count { s -> s == 'O' } }
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}

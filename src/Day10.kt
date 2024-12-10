enum class TrailDir {
    UP, DOWN, LEFT, RIGHT;

    val vec
        get() :  Pair<Int, Int> {
            return when (this) {
                UP -> -1 to 0
                DOWN -> 1 to 0
                LEFT -> 0 to -1
                RIGHT -> 0 to 1
            }

        }
}

fun findTrailRecursively(map: List<String>, pt: Pair<Int, Int>): List<List<Pair<Int, Int>>> {
    val (x, y) = pt
    if (map[x][y] == '9') {
        return listOf(listOf(pt))
    }
    val r = ArrayList<List<Pair<Int, Int>>>()
    for (dir in TrailDir.entries) {
        val (dx, dy) = dir.vec
        val (ptx, pty) = x + dx to y + dy
        if (ptx in map.indices && pty in map[0].indices && map[ptx][pty] - map[x][y] == 1) {
            for (trail in findTrailRecursively(map, ptx to pty)) {
                r.add(listOf(pt) + trail)
            }
        }
    }
    return r
}

fun findTrails(map: List<String>): List<List<List<Pair<Int, Int>>>> {
    val heads = map.indices.map { i ->
        map[i].indices.map { j ->
            i to j
        }
    }.flatten().filter { (i, j) -> map[i][j] == '0' }
    return heads.map { findTrailRecursively(map, it) }
}

fun main() {

    fun part1(input: List<String>): Int {
        return findTrails(input).sumOf { it.map { trail -> trail.last() }.toSet().count() }
    }

    fun part2(input: List<String>): Int {
        return findTrails(input).sumOf { it.count() }
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 36)
    check(part2(testInput) == 81)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
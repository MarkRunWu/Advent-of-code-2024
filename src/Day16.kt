


fun findMinCostOnMaze(map: MutableList<MutableList<Char>>, visitedSet: Set<Pair<Int,Int>>, pos: Pair<Int, Int>, dir: Dir, acc: Int): Int {
    return when(dir) {
        Dir.UP, Dir.DOWN -> listOf(dir, Dir.LEFT, Dir.RIGHT)
        Dir.LEFT, Dir.RIGHT-> listOf(dir, Dir.UP, Dir.DOWN)
    }.minOf {
        val (vx, vy) = pos + it.value
        if (vx !in map[0].indices || vy !in map.indices || map[vy][vx] == '#' || map[vy][vx] == '@') {
            Int.MAX_VALUE
        } else if (map[vy][vx] == 'E') {
            acc + 1
        } else {
            map[vy][vx] = '@'
            val v = findMinCostOnMaze(map, visitedSet, vx to vy, it, acc + 1 + if (it == dir) 0 else 1000)
            map[vy][vx] = '.'
            v
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        val map = input.map { it.toMutableList() }.toMutableList()
        return findMinCostOnMaze(map, HashSet(), 1 to input.size - 2, Dir.RIGHT, 0).also { println(map.joinToString("\n") {it.joinToString("") } ) }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput).also { println(it) } == 11048)
    check(part2(testInput) == 0)


    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
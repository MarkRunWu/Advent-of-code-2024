import kotlin.math.min


fun findMinCostOnMaze(map: MutableList<MutableList<Char>>, visitedSet: MutableSet<Pair<Int,Int>>, costMap: MutableList<MutableList<Int>>, pos: Pair<Int, Int>, dir: Dir, acc: Int): Int {
    val (vx, vy) = pos
    if (vx !in map[0].indices || vy !in map.indices || map[vy][vx] == '#') {
        return Int.MAX_VALUE
    } else if (map[vy][vx] == 'E') {
        return acc
    }
    if (acc > costMap[vy][vx]) {
        return Int.MAX_VALUE
    }
    costMap[vy][vx] = acc
    visitedSet.add(pos)
    val dirs = when (dir) {
        Dir.UP, Dir.DOWN -> listOf(dir, Dir.RIGHT, Dir.LEFT )
        Dir.LEFT, Dir.RIGHT -> listOf(dir, Dir.DOWN, Dir.UP )
    }
    var min = Int.MAX_VALUE
    var minVisited: HashSet<Pair<Int, Int>>? = null
    for (nextDir in dirs) {
         val localVisited = HashSet<Pair<Int, Int>>()
         val localMin = findMinCostOnMaze(
            map,
            localVisited,
            costMap,
            pos + nextDir.value,
            nextDir,
            acc + 1 + if (nextDir == dir) 0 else 1000
        )
        if (localMin < min) {
            min = localMin
            minVisited = localVisited
        }
    }
    if (minVisited != null) {
        visitedSet.addAll(minVisited)
    }
    return min
}

data class Path(val pos: Pair<Int, Int>, val cost: Int, val dir: Dir)

fun findMinCostOnMazeBFS(map: MutableList<MutableList<Char>>): Int {
    val queue = arrayListOf(Path(1 to map.size -2, 0, Dir.RIGHT))
    var minCost = Int.MAX_VALUE
    val visitedSet = HashSet<Pair<Pair<Int, Int>, Dir>>()
    var i = 0
    while (queue.isNotEmpty()) {
        val path = queue.removeFirst()
        if (map[path.pos.second][path.pos.first] == 'E' && path.cost < minCost) {
            minCost = path.cost
            continue
        }
        if (visitedSet.contains(path.pos to path.dir)) {
            continue
        }
        visitedSet.add(path.pos to path.dir)
        val nextQueue = when(path.dir) {
                Dir.RIGHT, Dir.LEFT -> listOf(path.dir, Dir.UP, Dir.DOWN)
                Dir.UP, Dir.DOWN -> listOf(path.dir, Dir.RIGHT, Dir.LEFT)
            }.map {
                val nextPos = path.pos + it.value
                Path(nextPos, path.cost + 1 + if(path.dir == it) 0 else 1000, it)
            }.filter { (pos ) ->  pos.first in map[0].indices && pos.second in map.indices && (map[pos.second][pos.first] == '.' || map[pos.second][pos.first]  == 'E') }
        queue.addAll(nextQueue.sortedBy { it.cost }.also { println(it) })
        i++
    }

    return minCost
}

fun main() {

    fun part1(input: List<String>): Int {
        val map = input.map { it.toMutableList() }.toMutableList()
        val costMap = input.map { it.map { Int.MAX_VALUE }.toMutableList() }.toMutableList()
        val visitedSet = HashSet<Pair<Int, Int>>()
        println(findMinCostOnMazeBFS(map))
        return findMinCostOnMaze(map,  visitedSet, costMap, 1 to map.size - 2, Dir.RIGHT, 0 ).also {
//            val sb = StringBuilder()
//            for (i in map.indices) {
//                if (i > 0) {
//                    sb.append("\n")
//                }
//                for (j in map[i].indices) {
//                    sb.append(if (visitedSet.contains(j to i)) {
//                        '*'
//                    } else {
//                        map[i][j]
//                    })
//                }
//            }
//            println(sb.toString())
        }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 7036)
    check(part2(testInput) == 0)


    val input = readInput("Day16")
    check(part1(input) == 107468)
    part2(input).println()
}
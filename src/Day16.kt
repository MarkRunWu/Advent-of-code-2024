data class Path(
    val pos: Pair<Int, Int>,
    val cost: Int,
    val dir: Dir,
    val points: Set<Pair<Pair<Int, Int>, Dir>>
)

fun findMinCostOnMaze(map: List<String>): Pair<List<Path>, Map<Pair<Pair<Int, Int>, Dir>, List<Path>>> {
    val start = 1 to map.size - 2
    val startPath = Path(start, 0, Dir.RIGHT, setOf(start to Dir.RIGHT))
    val queue = arrayListOf(startPath)
    val finishedCandidates = ArrayList<Path>()
    var minCost = Int.MAX_VALUE
    val visitedSet = HashSet<Pair<Pair<Int, Int>, Dir>>()
    val pathMap = HashMap<Pair<Pair<Int, Int>, Dir>, MutableList<Path>>()
    while (queue.isNotEmpty()) {
        val path = queue.minBy { it.cost }
        queue.remove(path)
        if (map[path.pos.second][path.pos.first] == 'E') {
            finishedCandidates.add(path)
            pathMap.getOrPut(path.pos to path.dir) { ArrayList() }.add(path)
            if (path.cost <= minCost) {
                minCost = path.cost
            }
            continue
        }
        if (visitedSet.contains(path.pos to path.dir)) {
            val diff = path.cost - pathMap[path.pos to path.dir]!!.first().cost
            if (diff == 0) {
                pathMap[path.pos to path.dir]!!.add(path)
            }
            continue
        }
        pathMap.getOrPut(path.pos to path.dir) { ArrayList() }.add(path)
        visitedSet.add(path.pos to path.dir)
        val nextQueue = when (path.dir) {
            Dir.RIGHT, Dir.LEFT -> listOf(path.dir, Dir.UP, Dir.DOWN)
            Dir.UP, Dir.DOWN -> listOf(path.dir, Dir.RIGHT, Dir.LEFT)
        }.map {
            val nextPos = path.pos + it.value
            Path(
                nextPos,
                path.cost + 1 + if (path.dir == it) 0 else 1000,
                it,
                path.points + (nextPos to it)
            )
        }
            .filter { (pos) -> pos.first in map[0].indices && pos.second in map.indices && map[pos.second][pos.first] != '#' }
        queue.addAll(nextQueue)
    }
    return finishedCandidates.filter { it.cost == minCost } to pathMap
}

fun main() {

    fun part1(input: List<String>): Int {
        val (path, _) = findMinCostOnMaze(input)
        return path.first().cost
    }

    fun part2(input: List<String>): Int {
        val (path, pathMap) = findMinCostOnMaze(input)
        val endPath = path.first()

        val branches = arrayListOf(endPath)
        val pts = HashSet<Pair<Pair<Int, Int>, Dir>>()
        while (branches.isNotEmpty()) {
            val p = branches.removeFirst()
            branches.addAll(p.points.filter { !pts.contains(it) }.map { pathMap[it]!! }
                .filter { it.size > 1 }.flatten())
            pts.addAll(p.points)
        }
        val sb = StringBuilder()
        for (i in input.indices) {
            if (i > 0) {
                sb.append('\n')
            }
            for (j in input[i].indices) {
                sb.append(
                    if (pts.any { it.first == j to i }) {
                        'O'
                    } else if (input[i][j] == '#') {
                        '#'
                    } else {
                        '.'
                    }
                )
            }
        }
        println(sb.toString())
        return sb.count { it == 'O' }
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 7036)
    check(part2(testInput) == 45)


    val input = readInput("Day16")
    check(part1(input) == 107468)
    part2(input).println()
}
import kotlin.math.sign

enum class FarmDir {

    UP, DOWN, LEFT, RIGHT;

    val vec: Pair<Int, Int>
        get() {
            return when (this) {
                UP -> 1 to 0
                DOWN -> -1 to 0
                LEFT -> 0 to -1
                RIGHT -> 0 to 1
            }
        }

    companion object {
        fun from(edge: Pair<Pair<Int, Int>, Pair<Int, Int>>): FarmDir {
            val (x1, y1) = edge.first
            val (x2, y2) = edge.second
            val dir = (x2 - x1).sign to (y2 - y1).sign
            return entries.first { it.vec == dir }
        }
    }
}


operator fun Pair<Int, Int>.plus(a: Pair<Int, Int>): Pair<Int, Int> {
    return first + a.first to second + a.second
}

data class FarmGroup(val kind: String, val plants: List<Pair<Int, Int>>) {
    private fun getEdges(map: List<List<String>>) = plants.flatMap { (x, y) ->
        FarmDir.entries.filter { dir ->
            val (dx, dy) = dir.vec
            val (posX, posY) = x + dx to y + dy
            posX < 0 || posX >= map.size || posY < 0 || posY >= map[0].size || map[posX][posY] != kind
        }.map { dir ->
            when (dir) {
                FarmDir.UP -> (x to y) to ((x to y) + FarmDir.RIGHT.vec)
                FarmDir.DOWN -> ((x to y) + FarmDir.DOWN.vec + FarmDir.RIGHT.vec) to ((x to y) + FarmDir.DOWN.vec)
                FarmDir.LEFT -> ((x to y) + FarmDir.DOWN.vec) to (x to y)
                FarmDir.RIGHT -> ((x to y) + FarmDir.RIGHT.vec) to ((x to y) + FarmDir.RIGHT.vec + FarmDir.DOWN.vec)
            }
        }
    }

    private fun calPerimeterOnMap(map: List<List<String>>): Int {
        return plants.sumOf { (x, y) ->
            FarmDir.entries.count { dir ->
                val (dx, dy) = dir.vec
                val (posX, posY) = x + dx to y + dy
                posX < 0 || posX >= map.size || posY < 0 || posY >= map[0].size || map[posX][posY] != kind
            }
        }
    }

    private fun calSidesOnMap(map: List<List<String>>): Int {
        val edges = getEdges(map).groupBy({ it.first }) { it.second }
        return edges.entries.sumOf { (p1, v) ->
            v.count { p2 ->
                FarmDir.from(p1 to p2) != FarmDir.from(p2 to edges[p2]!!.first())
            }
        }
    }

    fun calPriceOnMap(map: List<List<String>>) =
        plants.size * calPerimeterOnMap(map)

    fun calPriceWithBulkDiscountOnMap(map: List<List<String>>) =
        plants.size * calSidesOnMap(map)
}

fun Array<Array<Boolean>>.nonVisitedPos(): Pair<Int, Int>? {
    for (i in indices) {
        for (j in this[i].indices) {
            if (!this[i][j]) {
                return i to j
            }
        }
    }
    return null
}

fun findGroup(
    map: List<List<String>>,
    start: Pair<Int, Int>,
    visits: Array<Array<Boolean>>
): FarmGroup {
    val (originX, originY) = start
    visits[originX][originY] = true
    val kind = map[originX][originY]
    return FarmGroup(kind, ArrayList<Pair<Int, Int>>().apply {
        val queue = ArrayList<Pair<Int, Int>>().also { it.add(start) }
        while (queue.isNotEmpty()) {
            val nextQueue = ArrayList<Pair<Int, Int>>()
            for ((x, y) in queue) {
                for (dir in FarmDir.entries) {
                    val (dx, dy) = dir.vec
                    val (posX, posY) = x + dx to y + dy
                    if (posX in map.indices && posY in map[0].indices &&
                        !visits[posX][posY] && map[posX][posY] == kind
                    ) {
                        visits[posX][posY] = true
                        nextQueue.add(posX to posY)
                    }
                }
                add(x to y)
            }
            queue.clear()
            queue.addAll(nextQueue)
        }
    })
}

fun findGroups(map: List<List<String>>): List<FarmGroup> {
    val visits = Array<Array<Boolean>>(map.size, { Array<Boolean>(map[0].size, { false }) })
    val groups = ArrayList<FarmGroup>()
    while (true) {
        val pos = visits.nonVisitedPos()
        if (pos != null) {
            groups.add(findGroup(map, pos, visits))
        } else {
            break
        }
    }
    return groups
}

fun main() {

    fun part1(input: List<String>): Int {
        val map = input.map { it.map { it.toString() } }
        return findGroups(map).sumOf { it.calPriceOnMap(map) }
    }

    fun part2(input: List<String>): Int {
        val map = input.map { it.map { it.toString() } }
        return findGroups(map)
            .sumOf { it.calPriceWithBulkDiscountOnMap(map) }
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 1930)
    check(part2(testInput) == 1206)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
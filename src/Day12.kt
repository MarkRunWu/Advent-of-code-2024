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
}


operator fun Pair<Int, Int>.plus(a: Pair<Int, Int>): Pair<Int, Int> {
    return first + a.first to second + a.second
}

enum class SideDir {

    UP, LEFTUP, RIGHTUP, LEFTDOWN, RIGHTDOWN, DOWN, LEFT, RIGHT;


    val vec: Pair<Int, Int>
        get() {
            return when (this) {
                UP -> 1 to 0
                DOWN -> -1 to 0
                LEFT -> 0 to -1
                RIGHT -> 0 to 1
                LEFTUP -> LEFT.vec + UP.vec
                RIGHTUP -> RIGHT.vec + UP.vec
                LEFTDOWN -> LEFT.vec + DOWN.vec
                RIGHTDOWN -> RIGHT.vec + DOWN.vec
            }
        }

    fun rotated(): SideDir {
        return when (this) {
            UP -> RIGHTUP
            DOWN -> LEFTDOWN
            LEFT -> LEFTUP
            RIGHT -> RIGHTDOWN
            RIGHTUP -> RIGHT
            RIGHTDOWN -> DOWN
            LEFTDOWN -> LEFT
            LEFTUP -> UP
        }
    }
}

data class FarmGroup(val kind: String, val plants: List<Pair<Int, Int>>) {
    private fun getEdgePts(map: List<List<String>>) = plants.filter { (x, y) ->
        FarmDir.entries.any { dir ->
            val (dx, dy) = dir.vec
            val (posX, posY) = x + dx to y + dy
            posX < 0 || posX >= map.size || posY < 0 || posY >= map[0].size || map[posX][posY] != kind
        }
    }

    private fun calPerimeterOnMap(map: List<List<String>>): Int {
        return getEdgePts(map).sumOf { (x, y) ->
            FarmDir.entries.count { dir ->
                val (dx, dy) = dir.vec
                val (posX, posY) = x + dx to y + dy
                posX < 0 || posX >= map.size || posY < 0 || posY >= map[0].size || map[posX][posY] != kind
            }
        }
    }

    private fun calSidesOnMap(map: List<List<String>>): Int {
        val pts = getEdgePts(map)
        val sortedPts = pts.sortedBy { (x, y) -> y }.sortedBy { (x) -> x }

        val visits = HashSet<Pair<Int, Int>>()
        var (x, y) = pts.first()
        var nextDir = SideDir.UP
        visits.add(x to y)
        while (visits.size != pts.size) {
            val currentDir = nextDir
            do {
                val (dx, dy) = nextDir.vec
                val pt = sortedPts.firstOrNull {
                    !visits.contains(it) &&

                            it == x + dx to y + dy
                }
                if (pt != null) {
                    x = pt.first
                    y = pt.second
                    break
                }
                nextDir = nextDir.rotated()
            } while (nextDir != currentDir)
            visits.add(x to y)
        }
        return 0
    }

    fun calPriceOnMap(map: List<List<String>>) =
        calPerimeterOnMap(map).also { kotlin.io.println("$kind Perimeter: $it") } * plants.size.also {
            kotlin.io.println(
                "Area: $it"
            )
        }

    fun calPriceWithBulkDiscountOnMap(map: List<List<String>>) =
        plants.size * calSidesOnMap(map).also { kotlin.io.println("$kind Sides: $it") }
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
        return findGroups(map).also { println(it.size) }.sumOf { it.calPriceOnMap(map) }
    }

    fun part2(input: List<String>): Int {
        val map = input.map { it.map { it.toString() } }
        return findGroups(map).also { println(it.size) }
            .sumOf { it.calPriceWithBulkDiscountOnMap(map) }
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 1930)
    check(part2(testInput) == 0)

//    val input = readInput("Day12")
//    part1(input).println()
//    part2(input).println()
}
data class Box(val pts: List<Pair<Int, Int>>) {
    fun isHit(pos: Pair<Int, Int>): Boolean {
        return pts.any { it == pos }
    }

    fun gps(): Int {
        return pts.sortedBy { it.second }.minBy { it.first }.gps()
    }
}

data class WarehouseSetting(
    val robotOrigin: Pair<Int, Int>,
    val boxes: List<Box>,
    val wallMap: List<List<Boolean>>
)

fun WarehouseSetting.copyWith(
    origin: Pair<Int, Int>? = null,
    boxes: List<Box>? = null,
    wallMap: List<List<Boolean>>? = null
): WarehouseSetting {
    return WarehouseSetting(origin ?: robotOrigin, boxes ?: this.boxes, wallMap ?: this.wallMap)

}

fun WarehouseSetting.dumpMap(): String {
    val map = StringBuilder()
    for (i in wallMap.indices) {
        if (i > 0) {
            map.append("\n")
        }
        for (j in wallMap[i].indices) {
            if (wallMap[i][j]) {
                map.append("#")
            } else if (robotOrigin == j to i) {
                map.append("@")
            } else if (boxes.any { it.pts.first() == j to i && it.pts.size == 1 }) {
                map.append("O")
            } else if (boxes.any { it.pts.first() == j to i && it.pts.size == 2 }) {
                map.append("[")
            } else if (boxes.any { it.pts.last() == j to i && it.pts.size == 2 }) {
                map.append("]")
            } else {
                map.append(".")
            }
        }
    }
    return map.toString()
}

fun parseRobotMovements(input: List<String>): List<RobotDir> {
    return input.flatMap {
        it.filter { c -> c != '\n' }.map { c ->
            when (c) {
                '<' -> RobotDir.LEFT
                '>' -> RobotDir.RIGHT
                '^' -> RobotDir.UP
                'v' -> RobotDir.DOWN
                else -> throw IllegalStateException("Unrecognized movement symbol $c")
            }
        }
    }
}

fun parseMap(map: List<String>): WarehouseSetting {
    var robotPos: Pair<Int, Int> = 0 to 0
    val boxes = ArrayList<Box>()
    val wallMap = Array(map.size) { Array(map[0].length) { false } }
    for (i in map.indices) {
        var start: Pair<Int, Int> = 0 to 0
        for (j in map[0].indices) {
            if (map[i][j] == '@') {
                robotPos = j to i
            } else if (map[i][j] == 'O') {
                boxes.add(Box(listOf(j to i)))
            } else if (map[i][j] == '[') {
                start = j to i
            } else if (map[i][j] == ']') {
                boxes.add(Box(listOf(start, j to i)))
            } else if (map[i][j] == '#') {
                wallMap[i][j] = true
            }
        }
    }
    return WarehouseSetting(robotPos, boxes, wallMap.map { it.toList() }.toList())
}

operator fun Pair<Int, Int>.plus(dir: RobotDir): Pair<Int, Int> {
    val (dx, dy) = dir.v
    return first + dx to second + dy
}

fun Pair<Int, Int>.gps(): Int {
    return first + second * 100
}

fun simulateRobotMovements(
    initialSetting: WarehouseSetting,
    movements: List<RobotDir>
): WarehouseSetting {
    var currentSetting = initialSetting
    val wallMap = currentSetting.wallMap
    for (i in movements.indices) {
        val movement = movements[i]
        val (x, y) = currentSetting.robotOrigin + movement
        var boxes = currentSetting.boxes
        if (x in wallMap[0].indices && y in wallMap.indices && !wallMap[y][x]) {
            val mutBoxes = boxes.toMutableSet()
            val hitboxes = HashSet<Box>()
            var currentPos = x to y
            var detectingHitPts = listOf(x to y)
            do {
                if (detectingHitPts.any { (wx, wy) -> wallMap[wy][wx] }) {
                    break
                }
                val hits = mutBoxes.filter { box -> detectingHitPts.any { box.isHit(it) } }
                if (hits.isEmpty()) {
                    break
                }
                hitboxes.addAll(hits)
                mutBoxes.removeAll(hits.toSet())
                detectingHitPts = hits.flatMap { box ->
                    when (movement) {
                        RobotDir.LEFT -> listOf(box.pts.first())
                        RobotDir.RIGHT -> listOf(box.pts.last())
                        RobotDir.UP, RobotDir.DOWN -> box.pts
                    }
                }.map { it + movement }
                currentPos = when (movement) {
                    RobotDir.UP, RobotDir.DOWN -> currentPos.first to detectingHitPts.first().second
                    RobotDir.LEFT, RobotDir.RIGHT -> detectingHitPts.first().first to currentPos.second
                }
            } while (true)
            val origin = if (detectingHitPts.none { (wx, wy) -> wallMap[wy][wx] }) {
                boxes =
                    mutBoxes.toList() + hitboxes.map { box -> Box(box.pts.map { it + movement }) }
                x to y
            } else {
                currentSetting.robotOrigin
            }
            currentSetting = currentSetting.copyWith(origin, boxes)
        }
    }
    return currentSetting
}

enum class RobotDir {
    LEFT, RIGHT, UP, DOWN;

    val v: Pair<Int, Int>
        get() {
            return when (this) {
                LEFT -> -1 to 0
                RIGHT -> 1 to 0
                UP -> 0 to -1
                DOWN -> 0 to 1
            }
        }
}


fun main() {

    fun part1(input: List<String>): Int {
        val separatorIndex = input.indexOfFirst { it.isEmpty() }

        val warehouseSetting = parseMap(input.subList(0, separatorIndex))
        val movements = parseRobotMovements(input.subList(separatorIndex + 1, input.size))
        return simulateRobotMovements(
            warehouseSetting,
            movements
        ).also { it.dumpMap() }.boxes.sumOf {
            it.gps()
        }
    }

    fun part2(input: List<String>): Int {
        val separatorIndex = input.indexOfFirst { it.isEmpty() }
        val scaledMap = input.subList(0, separatorIndex).map {
            it.map { c ->
                when (c) {
                    '#' -> "##"
                    'O' -> "[]"
                    '@' -> "@."
                    else -> ".."
                }
            }.joinToString("")
        }
        val warehouseSetting = parseMap(scaledMap)
        check(warehouseSetting.dumpMap() == scaledMap.joinToString("\n").also { println(it) })
        val movements = parseRobotMovements(input.subList(separatorIndex + 1, input.size))
        return simulateRobotMovements(
            warehouseSetting,
            movements
        ).also { println(it.dumpMap()) }.boxes.sumOf { it.gps() }
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput) == 10092)
    check(part2(testInput) == 9021)


    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
data class Robot(
    val origin: Pair<Int, Int>,
    val vel: Pair<Int, Int>
) {
    companion object {
        fun parse(config: String): Robot {
            val (pos, vel) = config.split(" ").map { part ->
                val r = Regex("""(-?\d+),(-?\d+)""").find(part)!!.groupValues
                r[1].toInt() to r[2].toInt()
            }
            return Robot(pos, vel)
        }
    }

    fun copyWith(pos: Pair<Int, Int>): Robot {
        return Robot(pos, vel)
    }
}

fun Robot.predictPos(map: Pair<Int, Int>, iter: Int): Pair<Int, Int> {
    var (x, y) = origin
    val (vx, vy) = vel
    val (w, h) = map
    repeat(iter) {
        x = (w + x + vx) % w
        y = (h + y + vy) % h
    }
    return x to y
}

fun getRegionID(map: Pair<Int, Int>, pos: Pair<Int, Int>): Int {
    val (w, h) = map
    val (x, y) = pos

    return if (x < w / 2) {
        if (y < h / 2) {
            0
        } else {
            1
        }
    } else {
        if (y < h / 2) {
            2
        } else {
            3
        }
    }
}

fun hasXmasTreePattern(map: Array<Array<Boolean>>): Boolean {
    return map.mapIndexed { y, v ->
        y in 0..<map.size - 1 &&
                v.mapIndexed { x, v ->
                    x in 1..<map[0].size - 1 && map[y][x] && map[y + 1][x - 1] && map[y + 1][x + 1]
                }.any()
    }.any()
}

fun List<Pair<Int, Int>>.getGuardScore(map: Pair<Int, Int>): Int {
    val (divX, divY) = map.first / 2 to map.second / 2
    return groupBy {
        it
    }.mapValues { it.value.size }.asSequence()
        .filter { (k) -> k.first != divX && k.second != divY }
        .map {
            it.key to it.value
        }.groupBy({ getRegionID(map, it.first) }) { it.second }.map { it.value.sum() }
        .reduce { acc, i -> acc * i }
}


fun main() {

    fun part1(input: List<String>, map: Pair<Int, Int>, sec: Int = 100): Int {
        return input.map {
            Robot.parse(it)
        }.map {
            it.predictPos(map, sec)
        }.getGuardScore(map)
    }

    fun part2(input: List<String>, map: Pair<Int, Int>): Int {
        // finding min score strategy (learn from https://www.youtube.com/watch?v=ySUUTxVv31U)
        var minScore = Int.MAX_VALUE
        var minSec = 0
        var robots = input.map {
            Robot.parse(it)
        }
        for (s in 0..map.first * map.second) {
            robots = robots.map { it.copyWith(it.predictPos(map, 1)) }
            val score = robots.map { it.origin }.getGuardScore(map)
            if (score < minScore) {
                minScore = score
                minSec = s
            }
        }
        val drawingPane = Array(map.second) { Array(map.first) { false } }
        robots = robots.map { it.copyWith(it.predictPos(map, minSec)) }
        for (robot in robots) {
            val (x, y) = robot.origin
            drawingPane[y][x] = true
        }
        println(drawingPane.joinToString("\n") { it.joinToString("") { if (it) "|" else "_" } })
        return minSec

    }

    val testInput = readInput("Day14_test")
    check(part1(testInput, 11 to 7).also { println(it) } == 12)

    val input = readInput("Day14")
    part1(input, 101 to 103).println()
    part2(input, 101 to 103).println()
}
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
}

fun Robot.predictPos(map: Pair<Int, Int>, iter: Int): Pair<Int, Int> {
    var (x, y) = origin
    val (vx, vy) = vel
    val (w, h) = map
    repeat(iter) {
        x = (x + vx) % w
        y = (y + vy) % h
        if (x < 0) {
            x += w
        }
        if (y < 0) {
            y += h
        }
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


fun main() {

    fun part1(input: List<String>, map: Pair<Int, Int>): Int {
        val (divX, divY) = map.first / 2 to map.second / 2
        return input.map {
            Robot.parse(it)
        }.map {
            it.predictPos(map, 100)
        }.groupBy {
            it
        }.mapValues { it.value.size }.asSequence()
            .filter { (k) -> k.first != divX && k.second != divY }
            .map {
                it.key to it.value
            }.groupBy({ getRegionID(map, it.first) }) { it.second }.map { it.value.sum() }
            .reduce { acc, i -> acc * i }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput, 11 to 7).also { println(it) } == 12)
    check(part2(testInput) == 0)

    val input = readInput("Day14")
    part1(input, 101 to 103).println()
    part2(input).println()
}
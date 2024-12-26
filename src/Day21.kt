import kotlin.math.abs
import kotlin.math.sign


val numericKeypad = listOf(
    listOf( '7', '8', '9'),
    listOf( '4', '5', '6'),
    listOf( '1', '2', '3'),
    listOf( '#', '0', 'A'),
)

val directionalKeypad = listOf(
    listOf( '#', '^', 'A'),
    listOf( '<', 'V', '>'),
)

fun List<List<Char>>.posOf(char: Char): Pair<Int, Int>? {
    for (i in indices) {
        for(j in get(i).indices) {
            if (get(i)[j] == char) {
                return j to i
            }
        }
    }
    return null
}

fun List<List<Char>>.codeAt(pos: Pair<Int, Int>): Char {
    return get(pos.second)[pos.first]
}

fun getDirectionsOfCodes(keypad: List<List<Char>>, codes: String, steps: MutableMap<Pair<Char, Char>, List<String>> ): List<String> {
    var startPos = keypad.posOf('A')!!
    val gapPos = keypad.posOf('#')!!
    var result = emptyList<String>()
    for (code in codes) {
        val endPos = keypad.posOf(code)!!
        val actions = steps.getOrPut(keypad.codeAt(startPos) to code) {
            val (endX, endY ) = endPos
            val (startX, startY) = startPos
            val dx = endX - startX
            val dy = endY - startY
            val signX = dx.sign
            val signY = dy.sign
            if (signY == 0 && signX == 0) {
                return@getOrPut listOf("A")
            }
            val dirs = ArrayList<Dir>()
            repeat(abs(dx)) {
                dirs.add(
                    if(signX > 0) {
                        Dir.RIGHT
                    } else {
                        Dir.LEFT
                    }
                )
            }
            repeat(abs(dy)) {
                dirs.add(
                    if(signY > 0) {
                        Dir.DOWN
                    } else {
                        Dir.UP
                    }
                )
            }
            val combinations = HashSet<List<Dir>>()
            for (i in dirs.indices) {
                for (j in i..<dirs.size) {
                    val tmp = dirs[i]
                    dirs[i] = dirs[j]
                    dirs[j] = tmp
                    combinations.add(dirs.toList())
                }
            }
            combinations.filter {
                var pos = startPos
                for (dir in it) {
                    if (pos + dir.value == gapPos) {
                        return@filter false
                    }
                    pos += dir.value
                }
                true
            }.map {
                it.joinToString("") { s ->
                    when (s) {
                        Dir.UP -> "^"
                        Dir.RIGHT -> ">"
                        Dir.DOWN -> "V"
                        Dir.LEFT -> "<"
                    }
                } + 'A'
            }
        }
        result = if (result.isEmpty()) {
            actions
        } else {
            result.flatMap { r ->
                actions.map {
                    r + it
                }
            }
        }
        startPos = endPos
    }
    return result
}


fun main() {
    fun part1(input: List<String>): Int {
        val steps = HashMap<Pair<Char, Char>, List<String>>()
        var actions = input.map {
            Regex("""\d+""").find(it)!!.value.toInt() to getDirectionsOfCodes(numericKeypad, it, steps) }

        repeat(2) {
            actions = actions.map { it.first to it.second.flatMap { action -> getDirectionsOfCodes(directionalKeypad, action, steps) } }
        }
        return actions.sumOf { rc ->
                println("${rc.first} ${rc.second.minBy { it.length }.length}")
                rc.first * rc.second.minBy { it.length }.length }
    }

    fun part2(input: List<String>): Int {
        val steps = HashMap<Pair<Char, Char>, List<String>>()
        var actions = input.map {
            Regex("""\d+""").find(it)!!.value.toInt() to getDirectionsOfCodes(numericKeypad, it, steps) }
            repeat(25) {
                println("robot $it")
                actions = actions.map { (k, v) -> k to v.flatMap { action -> getDirectionsOfCodes(directionalKeypad, action, steps) } }
            }
            return actions.sumOf { rc ->
                println("${rc.first} ${rc.second.minBy { it.length }.length}")
                rc.first * rc.second.minBy { it.length }.length }
    }

    val testInput = readInput("Day21_test")
    check(part1(testInput).also { println(it) } == 126384)
    check(part2(testInput) == 0)

    val input = readInput("Day21")
    part1(input).println()
    part2(input).println()
}

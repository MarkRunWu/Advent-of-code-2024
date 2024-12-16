import java.text.NumberFormat
import java.util.Scanner
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.roundToLong

//fun lcm(a: Int, b: Int): Int {
//    return
//}
fun parsePosition(line: String): Pair<Int, Int> {
    val x = Regex("""X\+(\d+)""").find(line)!!.groupValues[1]
    val y = Regex("""Y\+(\d+)""").find(line)!!.groupValues[1]
    return x.toInt() to y.toInt()
}
fun parsePrizePosition(line: String): Pair<Int, Int> {
    val x = Regex("""X=(\d+)""").find(line)!!.groupValues[1]
    val y = Regex("""Y=(\d+)""").find(line)!!.groupValues[1]
    return x.toInt() to y.toInt()
}

//fun resolveEquation(a: Pair<Int, Int>, b: Pair<Int,Int>, prize: Pair<Int, Int>): Pair<Int, Int>? {
//    val (xa, ya) = a
//    val (xb, yb) = b
//    val (xPrize, yPrize) = prize
//    val ra = (yb * xPrize - xb * yPrize) / (yb * xa - ya * xb).toFloat()
//    val rb = (ya * xPrize - xa * yPrize) / (ya * xb - xa * yb).toFloat()
//    if (ra.toInt() - ra == 0f && rb.toInt() - rb == 0f ) {
//        return ra.toInt() to rb.toInt()
//    }
//    return null
//}
fun resolveEquationDouble(a: Pair<Double, Double>, b: Pair<Double,Double>, prize: Pair<Double, Double>): Pair<Double, Double>? {
    val (xa, ya) = a
    val (xb, yb) = b
    val (xPrize, yPrize) = prize
    val raDenominator = (yb * xa - ya * xb)
    val rbDenominator = (ya * xb - xa * yb)
    val xPra = xPrize / raDenominator
    val yPra = yPrize / raDenominator
    val xPrb = xPrize / rbDenominator
    val yPrb = yPrize / rbDenominator
    val ra = yb * xPra - xb * yPra
    val rb = ya * xPrb - xa * yPrb

    if (abs(ra - round(ra)) < 0.0001 && abs(rb - round(rb)) < 0.0001 ) {
        return ra to rb
    }
    return null
}

//fun verifyAnswer(a: Pair<Long, Long>, b: Pair<Long,Long>, prize: Pair<Long, Long>, answer: Pair<Long, Long>): Boolean {
//    return (a.first * answer.first + b.first *answer.second).also { println(it) } == prize.first &&
//            a.second * answer.first + b.second * answer.second == prize.second
//}

fun main() {

    fun part1(input: List<String>): Int {
        val equationInput = input.filter { it.isNotEmpty()  }
        val r = ArrayList<Pair<Double, Double>>()
        for(i in equationInput.indices step 3) {
            val a = parsePosition(equationInput[i])
            val b = parsePosition(equationInput[i + 1])
            val prize = parsePrizePosition(equationInput[i + 2])
            val answer = resolveEquationDouble(a.first.toDouble() to a.second.toDouble(),
                b.first.toDouble() to b.second.toDouble(), prize.first.toDouble() to prize.second.toDouble())
            if (answer != null) {
                r.add(answer)
            }
        }
        return r.also { println(it) }.sumOf { (a, b) -> (a * 3 + b).toInt() }
    }

    fun part2(input: List<String>): Long {
        val equationInput = input.filter { it.isNotEmpty()  }
        val r = ArrayList<Pair<Double, Double>>()
        for(i in equationInput.indices step 3) {
            val a = parsePosition(equationInput[i])
            val b = parsePosition(equationInput[i + 1])
            val prize = parsePrizePosition(equationInput[i + 2])
            val answer = resolveEquationDouble(a.first.toDouble() to a.second.toDouble(), b.first.toDouble() to b.second.toDouble(), prize.first + 10000000000000.0 to
            prize.second + 10000000000000.0)
            if (answer != null) {
                r.add(answer)
            }
        }
        return r.also { println(it) }.sumOf { (a, b) -> (a * 3 + b).roundToLong() }
    }

    val testInput = readInput("Day13_test")
    check(part1(testInput).also { println(it) } == 480)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
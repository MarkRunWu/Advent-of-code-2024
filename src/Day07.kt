enum class Operator {
    mul, plus, concat;

    fun apply(num: Long, num2: Long): Long {
        return when (this) {
            mul -> num * num2
            plus -> num + num2
            else -> "$num$num2".toLong()
        }
    }
}

fun isCalibrate(nums: List<Long>, total: Long, operators: List<Operator>): Boolean {

    val acc = ArrayList<Long>()
    acc.add(nums.first())
    for (i in 1..<nums.size) {
        val num = nums[i]
        val r = ArrayList<Long>()
        for (a in acc) {
            for (op in operators) {
                r.add(op.apply(a, num))
            }
        }
        acc.clear()
        acc.addAll(r)
    }
    return acc.contains(total)
}

fun main() {
    fun part1(input: List<String>): Long {
        return input.sumOf {
            val (total, equation) = it.split(": ")
            val nums = equation.split(" ")
            if (isCalibrate(
                    nums.map { n -> n.toLong() },
                    total.toLong(),
                    listOf(Operator.mul, Operator.plus)
                )
            ) {
                total.toLong()
            } else {
                0
            }
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf {
            val (total, equation) = it.split(": ")
            val nums = equation.split(" ")
            if (isCalibrate(
                    nums.map { n -> n.toLong() },
                    total.toLong(),
                    listOf(Operator.mul, Operator.plus, Operator.concat)
                )
            ) {
                total.toLong()
            } else {
                0
            }
        }
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 3749L)
    check(part2(testInput) == 11387L)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}

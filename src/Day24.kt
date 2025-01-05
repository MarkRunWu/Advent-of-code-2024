import com.sun.jdi.Value
import kotlin.math.pow

sealed class ValueHolder(private val label: String) {
    abstract fun getValue(): Boolean
}
class SignalValue(val label: String, private val value: Boolean): ValueHolder(label) {
    override fun getValue(): Boolean {
        return value
    }
}
class GateValue(val label: String, private val op: OP, private val params: List<ValueHolder>): ValueHolder(label) {
    override fun getValue(): Boolean {
        return op.apply(params.first(), params.last())
    }
}

class RefValue(val label: String, val gates: Map<String, ValueHolder>): ValueHolder(label) {
    override fun getValue(): Boolean {
        return gates[label]!!.getValue()
    }
}

sealed class OP {
    abstract fun apply(
        a: ValueHolder, b: ValueHolder): Boolean
}

class OPAND: OP() {
    override fun apply( a: ValueHolder, b: ValueHolder): Boolean {
        return a.getValue() && b.getValue()
    }
}
class OPXOR: OP() {
    override fun apply(a: ValueHolder, b: ValueHolder): Boolean {
        return a.getValue().xor(b.getValue())
    }
}
class OPOR: OP() {
    override fun apply(a: ValueHolder, b: ValueHolder): Boolean {
        return a.getValue().or(b.getValue())
    }
}

fun main() {
    fun parseOp(op: String): OP {
        return when(op) {
            "AND" -> OPAND()
            "XOR" -> OPXOR()
            "OR" -> OPOR()
            else -> throw IllegalStateException("invalid operator $op")
        }
    }

    fun processCircle(input: List<String>): Long {
        val registers: MutableMap<String, ValueHolder> = input.filter { it.contains(":") }.associate {
            val (label, value) = it.split(": ")
            label to SignalValue(label, value.toInt() == 1)
        }.toMutableMap()
        val gapOperatorPattern = Regex("""(\w+) (\w+) (\w+) -> (\w+)""")
        val gapMaps: Map<String, ValueHolder> = input.filter { it.contains("->") }.associate {
            val result = gapOperatorPattern.find(it)!!
            val (_, x, op, y, z) = result.groupValues
            z to GateValue(z, parseOp(op), listOf(RefValue(x, registers), RefValue(y, registers)))
        }.toMap()
        registers.putAll(gapMaps)
        val outputPattern = Regex("""z\d+""")
        return registers.keys.asSequence().filter { outputPattern.matches(it) }.sorted().map {
            registers[it]!!.getValue()
        }.mapIndexed { i, v ->
            if(v) 2.0.pow(i).toLong() else 0
        }.sum()
    }
    fun day1(input: List<String>): Long{
        return processCircle(input)
    }

    fun day2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day24_test")

    check(day1(testInput) == 2024L)

    val input = readInput("Day24")
    day1(input).println()
    day2(input).println()
}
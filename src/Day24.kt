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

    fun parseGateMap(input: List<String>): Map<String, ValueHolder> {
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
        return registers
    }

    fun processCircle(input: List<String>): Long {
        val outputPattern = Regex("""z\d+""")
        val registers = parseGateMap(input)
        return registers.keys.asSequence().filter { outputPattern.matches(it) }.sorted().map {
            registers[it]!!.getValue()
        }.mapIndexed { i, v ->
            if(v) 2.0.pow(i).toLong() else 0
        }.sum()
    }

    fun evaluateWrongOutputPins(registers: Map<String, ValueHolder>): Int {
        val x = registers.keys.asSequence().filter { Regex("""x\d+""").matches(it) }.sorted().fold("") { acc, s ->
            (if (registers[s]!!.getValue()) '1' else '0') + acc
        }
        val y = registers.keys.asSequence().filter { Regex("""y\d+""").matches(it) }.sorted().fold("") { acc, s ->
            (if (registers[s]!!.getValue()) '1' else '0') + acc
        }
        val output = registers.keys.asSequence().filter { Regex("""z\d+""").matches(it) }.sorted().fold("") { acc, s ->
            (if (registers[s]!!.getValue()) '1' else '0') + acc
        }
        var carry = 0
        var expectedOutput = x.zip(y).fold("") { acc, (l, r) ->
            val sum = (l - '0') + (r - '0') + carry
            carry = sum / 2
            ('0' + (sum % 2)) + acc
        }
        expectedOutput = if (carry > 0) {
            '1'
        } else {
            '0'
        } + expectedOutput
        return output.zip(expectedOutput).count { (l, r) -> l != r }
    }

    fun day1(input: List<String>): Long{
        return processCircle(input)
    }

    fun correctPins(gateMap: Map<String, ValueHolder>, correctedWired: List<String>, wires: List<String>, level: Int, maxLevel: Int): List<String>? {
        if(level == maxLevel) {
            return if (evaluateWrongOutputPins(gateMap) == 0) {
                correctedWired
            } else {
                null
            }
        }
        for (i in wires.indices) {
            for (j in i + 1..<wires.size) {
                val gates = gateMap.toMutableMap()
                val tmp = gates[wires[i]]!!
                gates[wires[i]] = gates[wires[j]]!!
                gates[wires[j]] = tmp
                val w = wires.toMutableList().also {  it.removeAll(listOf(wires[i], wires[j])) }
                val result = correctPins(gates, correctedWired + wires[i] + wires[j], w, level + 1, maxLevel)
                if (result != null) {
                    return result
                }
            }
        }
        return null
    }

    fun day2(input: List<String>): String {
        val registers = parseGateMap(input)
        val wireLabels = registers.keys.filterNot { Regex("""(x|y)\d+""").matches(it) }.also { println(it) }
        return correctPins(registers, emptyList(), wireLabels, 0, 4 )!!.sorted().joinToString()
    }

    val testInput = readInput("Day24_test")

    check(day1(testInput) == 2024L)

    val input = readInput("Day24")
    day1(input).println()
    day2(input).println()
}
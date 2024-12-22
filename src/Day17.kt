import kotlin.math.pow

class Operand(val code: Int) {
    fun getValue(
        registers: Map<Char, Long>,
    ): Long {
        if (code in 0..3) {
            return code.toLong()
        }
        if (code in 4..6) {
            return registers[
                'A' + (code - 4)
            ]!!
        }
        throw IllegalStateException("Not supported operand with value $code")
    }

    override fun toString(): String {
        return if (code in 4..6) {
            return ('A' + code - 4).toString()
        } else {
            code.toString()
        }
    }
}


sealed class Result

class JumpTo(val position: Int) : Result()
class OutputValue(val value: Long) : Result()

sealed class Command {
    abstract fun run(registers: MutableMap<Char, Long>, operand: Operand): Result?

    companion object {
        fun from(v: Int): Command {
            return when (v) {
                0 -> ADV()
                1 -> BXL()
                2 -> BST()
                3 -> JNZ()
                4 -> BXC()
                5 -> OUT()
                6 -> BDV()
                7 -> CDV()
                else -> throw IllegalStateException("Not supported command with value $v")
            }
        }
    }

    class ADV : Command() {
        override fun run(registers: MutableMap<Char, Long>, operand: Operand): Result? {
            val numerator = registers['A']!!
            val denominator = 2.0.pow(operand.getValue(registers).toDouble())
            registers['A'] = (numerator / denominator).toLong()
            return null
        }
    }

    class BXL : Command() {
        override fun run(registers: MutableMap<Char, Long>, operand: Operand): Result? {
            registers['B'] = registers['B']!!.xor(operand.code.toLong())
            return null
        }
    }

    class BST : Command() {
        override fun run(registers: MutableMap<Char, Long>, operand: Operand): Result? {
            registers['B'] = operand.getValue(registers).mod(8L)
            return null
        }
    }

    class JNZ : Command() {
        override fun run(registers: MutableMap<Char, Long>, operand: Operand): Result? {
            return if (registers['A'] != 0L) {
                JumpTo(operand.getValue(registers).toInt())
            } else {
                null
            }
        }
    }

    class BXC : Command() {
        override fun run(registers: MutableMap<Char, Long>, operand: Operand): Result? {
            registers['B'] = registers['B']!!.xor(registers['C']!!)
            return null
        }
    }

    class OUT : Command() {
        override fun run(registers: MutableMap<Char, Long>, operand: Operand): Result {
            return OutputValue(operand.getValue(registers).mod(8L))
        }
    }

    class BDV : Command() {
        override fun run(registers: MutableMap<Char, Long>, operand: Operand): Result? {
            val numerator = registers['A']!!
            val denominator = 2.0.pow(operand.getValue(registers).toDouble())
            registers['B'] = (numerator / denominator).toLong()
            return null
        }
    }

    class CDV : Command() {
        override fun run(registers: MutableMap<Char, Long>, operand: Operand): Result? {
            val numerator = registers['A']!!
            val denominator = 2.0.pow(operand.getValue(registers).toDouble())
            registers['C'] = (numerator / denominator).toLong()
            return null
        }
    }

    override fun toString(): String {
        return this.javaClass.simpleName
    }
}

fun parseRegisters(input: List<String>): MutableMap<Char, Long> {
    val registers = HashMap<Char, Long>()
    val pattern = Regex("""Register (\w): (\d+)""")
    for (st in input.filter {
        it.startsWith("Register")
    }) {
        val r = pattern.find(st)!!
        registers[r.groupValues[1][0]] = r.groupValues[2].toLong()
    }
    return registers
}

fun parseProgram(input: List<String>): Pair<List<Command>, List<Operand>> {
    val st = input.first { it.startsWith("Program:") }
    val (_, nums) = st.split(" ")
    val values = nums.split(",").map { it.toInt() }
    val commands = ArrayList<Command>()
    val operands = ArrayList<Operand>()
    for (i in values.indices step 2) {
        commands.add(Command.from(values[i]))
        operands.add(Operand(values[i + 1]))
    }
    return commands to operands
}

fun runPrograms(
    commands: List<Command>,
    operands: List<Operand>,
    registers: MutableMap<Char, Long>
): String {
    var i = 0
    val results = ArrayList<Long>()
    while (i < commands.size) {
        val command = commands[i]
        val operand = operands[i]
        val result = command.run(registers, operand)
        when (result) {
            is JumpTo -> {
                i = result.position
                continue
            }

            is OutputValue -> results.add(result.value)
            null -> {}
        }
        i += 1
    }
    return results.joinToString(",")
}


fun main() {

    fun part1(input: List<String>): String {
        val registers = parseRegisters(input)
        val (commands, operands) = parseProgram(input)
        return runPrograms(commands, operands, registers)
    }

    fun part2(input: List<String>): Long {
        val initialRegisters = parseRegisters(input)
        val (commands, operands) = parseProgram(input)
        val program = input.first { it.startsWith("Program:") }.split(" ")[1]
        var initialA = 1L
        while (true) {
            val registers = initialRegisters.toMutableMap()
            registers['A'] = initialA
            val result = runPrograms(commands, operands, registers)
            println("${commands.zip(operands)}")
            println("$result $program $initialA")
            if (result.length < program.length) {
                initialA *= 2
            } else {
                initialA++
            }
        }
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput).also { println(it) } == "4,6,3,5,6,3,5,2,1,0")
    check(part2(testInput) == 117440L)


    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}
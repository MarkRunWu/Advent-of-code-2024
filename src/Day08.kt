fun produceAntinodes(a: Pair<Int, Int>, b: Pair<Int, Int>): List<Pair<Int, Int>> {
    val (ax, ay) = a
    val (bx, by) = b
    val (vx, vy) = ax - bx to ay - by

    return listOf(ax + vx to ay + vy, bx - vx to by - vy)
}

fun produceAntinodesRippleEffect(
    a: Pair<Int, Int>,
    b: Pair<Int, Int>,
    region: Pair<Int, Int>
): List<Pair<Int, Int>> {
    val (ax, ay) = a
    val (bx, by) = b
    val (vx, vy) = ax - bx to ay - by
    val r = ArrayList<Pair<Int, Int>>()
    r.add(a)
    r.add(b)
    var (forwardNodeX, forwardNodeY) = ax + vx to ay + vy
    var (backwardNodeX, backwardNodeY) = bx - vx to by - vy
    val (m, n) = region
    while ((forwardNodeX in 0..<m && forwardNodeY in 0..<n) ||
        (backwardNodeX in 0..<m && backwardNodeY in 0..<n)
    ) {
        if (forwardNodeX in 0..<m && forwardNodeY in 0..<n) {
            r.add(forwardNodeX to forwardNodeY)
        }
        if (backwardNodeX in 0..<m && backwardNodeY in 0..<n) {
            r.add(backwardNodeX to backwardNodeY)
        }
        forwardNodeX += vx
        forwardNodeY += vy
        backwardNodeX -= vx
        backwardNodeY -= vy
    }
    return r
}

fun findAllAnennases(input: List<String>): Map<Char, List<Pair<Int, Int>>> {
    return List(input.size) { i ->
        input[i].mapIndexed { j, c ->
            if (c != '.') {
                c to (i to j)
            } else null
        }
    }.flatten().filterNotNull().groupBy({ it.first }) { it.second }
}

fun main() {
    fun part1(input: List<String>): Int {
        return findAllAnennases(input).values.flatMap { annenases ->
            annenases.associateWith { k -> annenases.filter { it != k } }.flatMap { (k, v) ->
                v.flatMap { produceAntinodes(it, k) }
            }
        }.toSet().count { (x, y) -> x in input.indices && y in input[0].indices }
    }

    fun part2(input: List<String>): Int {
        val region = input.size to input[0].length
        return findAllAnennases(input).values.flatMap { annenases ->
            annenases.associateWith() { k -> annenases.filter { it != k } }.flatMap { (k, v) ->
                v.flatMap { produceAntinodesRippleEffect(it, k, region) }
            }
        }.toSet().count()
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 14)
    check(part2(testInput) == 34)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}

fun isCorrectOrdering(pages: List<String>, rules: Map<String, List<String>>): Boolean {
    return pages.mapIndexed { index, v -> index to v }.all {
        val (index, v) = it
        val rule = rules[v] ?: return@all true
        for (i in pages.indices) {
            if (rule.contains(pages[i]) && i < index) {
                return@all false
            }
        }
        true
    }
}


fun resolveDepOrders(depsMap: Map<String, List<String>>, visited: HashSet<String>, start: String): List<String> {
    if (visited.contains(start)) {
        return emptyList()
    }
    val deps = depsMap[start] ?: return listOf(start)
    val l = ArrayList<String>()
    for (dep in deps) {
        l.addAll(resolveDepOrders(depsMap, visited, dep))
        visited.add(dep)
    }
    l.add(start)
    visited.add(start)
    return l
}

fun correctOrder(pages: List<String>, rules: Map<String, List<String>>): List<String> {
    val depsMap = pages.asSequence().mapIndexed { index, v -> index to v }.map {
        val (index, v) = it
        val rule = rules[v] ?: return@map null
        val deps = arrayListOf<Pair<String, String>>()
        for (i in pages.indices) {
            if (rule.contains(pages[i]) && i < index) {
                deps.add(pages[i] to v)
            }
        }
        deps.ifEmpty {
            null
        }
    }.filterNotNull().flatten().groupBy({ it.first }) { it.second }
    val visited = HashSet<String>()
    return pages.indices.mapNotNull { i ->
        if (visited.contains(pages[i])) {
            null
        } else {
            resolveDepOrders(depsMap, visited, pages[i])
        }
    }.flatten()
}

fun main() {
    fun part1(input: List<String>): Int {
        val (rules, pages) = input.filter { it.isNotEmpty() }.partition { it.contains("|") }
        val ruleMap = rules.map { it.split("|") }.map { it[0] to it[1] }.groupBy({ it.first }) { it.second }
        return pages.map { it.split(",") }.filter { isCorrectOrdering(it, ruleMap) }
            .sumOf { it[it.size / 2].toInt() }
    }

    fun part2(input: List<String>): Int {
        val (rules, pages) = input.filter { it.isNotEmpty() }.partition { it.contains("|") }
        val ruleMap =
            rules.map { it.split("|") }.map { it[0] to it[1] }.groupBy({ it.first }) { it.second }
        return pages.map { it.split(",") }.filter { !isCorrectOrdering(it, ruleMap) }.map { correctOrder(it, ruleMap) }
            .sumOf { it[it.size / 2].toInt() }
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}

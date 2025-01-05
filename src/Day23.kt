fun findGroups(
    nodeMap: Map<String, List<String>>,
    currentNode: String,
    visits: MutableSet<Set<String>>,
    groups: Set<String>, minSize: Int = 0
): List<Set<String>> {
    if (visits.contains(groups)) {
        return emptyList()
    }
    val r = ArrayList<Set<String>>()
    val connections = nodeMap[currentNode]!!
    if (minSize > 0 && groups.size == minSize) {
        return listOf(groups)
    }
    visits.add(groups)
    for (con in connections) {
        if (!groups.contains(con) && groups.all { nodeMap[con]!!.contains(it) }) {
            for (subgroup in findGroups(nodeMap, con, visits, groups + con, minSize)) {
                r.add(subgroup)
            }
        }
    }
    if (r.isEmpty()) {
        return listOf(groups)
    }
    return r
}


fun main() {
    fun day1(input: List<String>): Int {
        val nodeMap = input.flatMap {
            val (a, b) = it.split("-")
            listOf(a to b, b to a)
        }.groupBy({ (a, _) -> a }) { it.second }

        return nodeMap.keys.flatMap { findGroups(nodeMap, it, HashSet(), setOf(it), 3) }.toSet()
            .count {
                it.size == 3 && it.any { s -> s.startsWith('t') }
            }
    }

    fun day2(input: List<String>): String {
        val nodeMap = input.flatMap {
            val (a, b) = it.split("-")
            listOf(a to b, b to a)
        }.groupBy({ (a, _) -> a }) { it.second }

        return nodeMap.keys.flatMap { findGroups(nodeMap, it, HashSet(), setOf(it)) }.toSet()
            .maxBy {
                it.size
            }.sorted().joinToString(",")
    }
    val testInput = readInput("Day23_test")

    check(day1(testInput) == 7)
    check(day2(testInput) == "co,de,ka,ta")

    val input = readInput("Day23")
    day1(input).println()
    day2(input).println()
}
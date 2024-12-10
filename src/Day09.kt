fun String.compact(): List<String> {
    val fileLayout = flatMapIndexed { index, c ->
        val num = c - '0'
        val serialNum = index / 2

        ArrayList<String>().apply {
            repeat(num) {
                if ((index + 1) % 2 == 0) {
                    add(".")
                } else {
                    add(serialNum.toString())
                }
            }
        }
    }.toMutableList()
    var headEmptyIndex = fileLayout.indexOfFirst { it == "." }
    var tailFileIndex = fileLayout.size - 1
    while (headEmptyIndex >= 0 && headEmptyIndex != tailFileIndex) {
        if (fileLayout[tailFileIndex] != "." && fileLayout[headEmptyIndex] == ".") {
            val tmp = fileLayout[tailFileIndex]
            fileLayout[tailFileIndex] = fileLayout[headEmptyIndex]
            fileLayout[headEmptyIndex] = tmp
            headEmptyIndex++
            tailFileIndex--
        } else {
            if (fileLayout[tailFileIndex] == ".") {
                tailFileIndex--
            }
            if (fileLayout[headEmptyIndex] != ".") {
                headEmptyIndex++
            }
        }
    }
    return fileLayout
}

fun List<String>.stringSpanOf(value: String) =
    iterator {
        var indexStart = 0
        while (indexStart in indices) {
            if (get(indexStart) == value) {
                var indexEnd = indexStart + 1
                while (indexEnd in indices && get(indexEnd) == value) {
                    indexEnd++
                }
                yield(IntRange(indexStart, indexEnd))
                indexStart = indexEnd
            } else {
                indexStart++
            }
        }
    }


fun List<String>.firstSpaceSpanRangeOrNull(size: Int, beforeIndex: Int): IntRange? {
    for (r in stringSpanOf(".")) {
        if (r.first >= beforeIndex) {
            return null
        }
        if (r.last - r.first >= size) {
            return r
        }
    }
    return null
}

fun List<String>.fileRange(id: String): IntRange {
    return stringSpanOf(id).next()
}

fun String.compactV2(): List<String> {
    val fileLayout = flatMapIndexed { index, c ->
        val num = c - '0'
        val serialNum = index / 2

        ArrayList<String>().apply {
            repeat(num) {
                if ((index + 1) % 2 == 0) {
                    add(".")
                } else {
                    add(serialNum.toString())
                }
            }
        }
    }.toMutableList()
    val headEmptyIndex = fileLayout.indexOfFirst { it.contains(".") }
    var tailIndex = fileLayout.size - 1
    while (headEmptyIndex > 0 && tailIndex >= 0) {
        val tailFileId = fileLayout[tailIndex]
        if (tailFileId != ".") {
            val tailFileIdSpan = fileLayout.fileRange(tailFileId)
            val size = tailFileIdSpan.last - tailFileIdSpan.first
            val emptySpaceSpan = fileLayout.firstSpaceSpanRangeOrNull(size, tailFileIdSpan.first)
            if (emptySpaceSpan != null) {
                var emptyIndex = emptySpaceSpan.first
                for (i in tailFileIdSpan.first..<tailFileIdSpan.last) {
                    val tmp = fileLayout[i]
                    fileLayout[i] = fileLayout[emptyIndex]
                    fileLayout[emptyIndex] = tmp
                    emptyIndex++
                }
            }
            tailIndex = tailFileIdSpan.first - 1
        } else {
            tailIndex--
        }
    }
    return fileLayout
}

fun List<String>.checksum(): Long {
    return mapIndexed { index, s ->
        if (s != ".") {
            s.toLong() * index
        } else {
            0
        }
    }.sum()
}

fun main() {
    fun part1(input: List<String>): Long {
        return input.map { c -> c.compact() }.sumOf { it.checksum() }
    }

    fun part2(input: List<String>): Long {
        return input.map { c -> c.compactV2() }.sumOf { it.checksum() }
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 1928L)
    check(part2(testInput) == 2858L)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}

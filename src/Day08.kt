fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { line -> line.split(" | ").map { it.split(" ") } }
            .map { it.first() to it.last() }
            .sumOf { it.second.count(::isUniqueDigit) }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { line -> line.split(" | ").map { it.split(" ") } }
            .map { it.first() to it.last() }
            .sumOf(::solveWiring)
    }

    val testInput = readInputLines("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInputLines("Day08")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

fun isUniqueDigit(digit: String) = when (digit.length) {
    2,3,4,7 -> true
    else -> false
}

/*
     0000
    5    1
    5    1
     6666
    4    2
    4    2
     3333

 0: 0111111
 1: 0000110
 2: 1011011
 3: 1001111
 4: 1100110
 5: 1101101
 6: 1111101
 7: 0000111
 8: 1111111
 9: 1101111

 */

val sevenSegmentMap = mapOf(
    0b0111111 to 0,
    0b0000110 to 1,
    0b1011011 to 2,
    0b1001111 to 3,
    0b1100110 to 4,
    0b1101101 to 5,
    0b1111101 to 6,
    0b0000111 to 7,
    0b1111111 to 8,
    0b1101111 to 9
)

fun solveWiring(wiring: Pair<List<String>, List<String>>): Int {
    val (signalWires, numbers) = wiring

    val segMap = buildSegmentMap(signalWires)

    return numbers.map { it.fold(0) { num, segment -> num or (segMap[segment] ?: 0) } }
        .map { sevenSegmentMap[it]!! }
        .joinToString("") { it.toString() }
        .toInt()
}

private fun buildSegmentMap(signalWires: List<String>): Map<Char, Int> {
    val one = signalWires.findOrThrow { it.length == 2 }
    val seven = signalWires.findOrThrow { it.length == 3 }
    val four = signalWires.findOrThrow { it.length == 4 }
    val eight = signalWires.findOrThrow { it.length == 7 }
    val three = signalWires.findOrThrow { it.length == 5 && one.all { oneSegment -> oneSegment in it } }

    val seg0 = seven.first { it !in one }
    val seg5 = four.first { it !in three }
    val five = signalWires.findOrThrow { it.length == 5 && seg5 in it }

    val seg1 = one.first { it !in five }
    val seg2 = one.first { it != seg1 }
    val seg6 = four.first { it !in one && it != seg5 }
    val seg3 = three.first { it != seg0 && it != seg1 && it != seg2 && it != seg6 }
    val seg4 = eight.first { it != seg0 && it != seg1 && it != seg2 && it != seg3 && it != seg5 && it != seg6 }

    return mapOf(
        seg0 to 1.shl(0),
        seg1 to 1.shl(1),
        seg2 to 1.shl(2),
        seg3 to 1.shl(3),
        seg4 to 1.shl(4),
        seg5 to 1.shl(5),
        seg6 to 1.shl(6)
    )
}

fun <T> List<T>.findOrThrow(predicate: (T) -> Boolean): T = find(predicate) ?: throw IllegalStateException("Item was not found in the list")
fun main() {
    fun part1(input: List<String>): Int {
        return input.asSequence()
            .map { it.toInt() }
            .windowed(size = 2)
            .count { it.first() < it.last() }
    }

    fun part2(input: List<String>): Int {
        return input.asSequence()
            .map { it.toInt() }
            .windowed(size = 3)
            .map { it.sum() }
            .windowed(size = 2)
            .count { it.first() < it.last() }
    }

    val testInput = readInputLines("Day01_test")
    check(part1(testInput) == 7)

    val input = readInputLines("Day01")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

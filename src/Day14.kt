fun main() {
    fun mergeMaps(mapA: Map<Char, Long>, mapB: Map<Char, Long>): Map<Char, Long> {
        return (mapA.asSequence() + mapB.asSequence())
            .groupBy({ it.key }, { it.value })
            .mapValues { (_, values) -> values.sum() }
    }

    fun countElements(pair: String, cycle: Int, insertionRules: Map<String, String>, cache: MutableMap<String, Map<Char, Long>> = mutableMapOf()): Map<Char, Long> {
        if (cycle == 0) {
            return mapOf(pair.first() to 1L)
        }

        val cycleKey = "$cycle$pair"
        cache[cycleKey]?.let { return it }

        val element = insertionRules[pair] ?: throw Exception("No insertion rule for '$pair'")

        val totalMap = mergeMaps(
            countElements("${pair.first()}$element", cycle - 1, insertionRules, cache),
            countElements("$element${pair.last()}", cycle - 1, insertionRules, cache)
        )
        cache[cycleKey] = totalMap
        return totalMap
    }

    fun runPolymerInsertion(polymerTemplate: String, insertionRules: Map<String, String>, cycles: Int): Map<Char, Long> {
        val cache = mutableMapOf<String, Map<Char, Long>>()
        val counts = polymerTemplate.windowed(2).map { countElements(it, cycles, insertionRules, cache) }
            .reduce { acc, map -> mergeMaps(acc, map) }

        return mergeMaps(counts, mapOf(polymerTemplate.last() to 1))
    }

    fun parseInput(input: List<String>): Pair<String, Map<String, String>> {
        val polymerTemplate = input.first()
        val insertionRules = input.drop(2)
            .map { it.split(" -> ") }
            .associate { it.first() to it.last() }

        return polymerTemplate to insertionRules
    }

    fun part1(input: List<String>): Long {
        val (polymerTemplate, insertionRules) = parseInput(input)

        val counts = runPolymerInsertion(polymerTemplate, insertionRules, cycles = 10)
        return counts.maxOf { it.value } - counts.minOf { it.value }
    }

    fun part2(input: List<String>): Long {
        val (polymerTemplate, insertionRules) = parseInput(input)

        val counts = runPolymerInsertion(polymerTemplate, insertionRules, cycles = 40)
        return counts.maxOf { it.value } - counts.minOf { it.value }
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588L)
    check(part2(testInput) == 2188189693529L)

    val input = readInput("Day14")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
import kotlin.math.abs
import kotlin.math.pow

fun calcLinearFuelCost(position: Int, positions: List<Int>) = positions.sumOf { abs(it - position) }
fun calcPolynomialFuelCost(position: Int, positions: List<Int>) = positions.sumOf {
    val distance = abs(it - position)
    (distance.toDouble().pow(2).toInt() + distance) / 2
}

fun main() {
    fun part1(input: List<String>): Int? {
        val positions = input.first().split(",").map { it.toInt() }.sorted()
        val minPos = positions.first()
        val maxPos = positions.last()
        return (minPos..maxPos).map { calcLinearFuelCost(it, positions) }.minByOrNull { it }
    }

    fun part2(input: List<String>): Int? {
        val positions = input.first().split(",").map { it.toInt() }.sorted()
        val minPos = positions.first()
        val maxPos = positions.last()
        return (minPos..maxPos).map { calcPolynomialFuelCost(it, positions) }.minByOrNull { it }
    }

    val testInput = readInputLines("Day07_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInputLines("Day07")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
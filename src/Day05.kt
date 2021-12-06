import kotlin.math.abs

fun inputToLineSegments(input: List<String>): List<LineSegment> {
    val inputRegex = "(\\d+),(\\d+) -> (\\d+),(\\d+)".toRegex()
    return input.map {
        inputRegex.matchEntire(it)?.destructured?.let {
                (x1, y1, x2, y2) -> LineSegment(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
        } ?: throw IllegalStateException("Bad input '$it'")
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return inputToLineSegments(input).asSequence()
            .filter { it.horizontal || it.vertical }
            .flatMap { it.points() }
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }
            .count()
    }

    fun part2(input: List<String>): Int {
        return inputToLineSegments(input).asSequence()
            .flatMap { it.points() }
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }
            .count()
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

data class LineSegment(val x1: Int, val y1: Int, val x2: Int, val y2: Int) {

    val horizontal: Boolean
        get() = y1 == y2

    val vertical: Boolean
        get() = x1 == x2

    fun points(): List<Point> {
        return if (horizontal) {
            val a1 = if (x1 < x2) x1 else x2
            val a2 = if (x1 >= x2) x1 else x2
            (a1..a2).map { Point(it, y1) }
        } else if (vertical) {
            val b1 = if (y1 < y2) y1 else y2
            val b2 = if (y1 >= y2) y1 else y2
            (b1..b2).map { Point(x1, it) }
        } else {
            val dx = (x2 - x1) / abs(x2 - x1)
            val dy = (y2 - y1) / abs(y2 - y1)
            generateSequence(x1) { if (it == x2) null else it + dx }
                .zip(generateSequence(y1) { if (it == y2) null else it + dy })
                .map { Point(it.first, it.second) }
                .toList()
        }
    }
}

data class Point(val x: Int, val y: Int)
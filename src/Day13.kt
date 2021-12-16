fun main() {
    fun parseInput(input: List<String>): Pair<List<Dot>, List<Instruction>> {
        val blankLine = input.indexOfFirst { it.isEmpty() }
        val dots = input.subList(0, blankLine)
            .map { it.split(",") }
            .map { Dot(it.first().toInt(), it.last().toInt()) }

        val instructions = input.subList(blankLine + 1, input.size)
            .map { it.substringAfter("fold along ") }
            .map { it.split("=") }
            .map { Instruction(Axis.valueOf(it.first().uppercase()), it.last().toInt()) }

        return dots to instructions
    }

    fun printDots(dots: Iterable<Dot>) {
        val display = Array(dots.maxOf { it.y } + 1) { Array(dots.maxOf { it.x } + 1) { " " } }
        dots.forEach { display[it.y][it.x] = "#" }
        display.forEach { line -> println(line.joinToString("") { it }) }
    }

    fun fold(dots: List<Dot>, instruction: Instruction): List<Dot> {
        return dots.map { instruction.evaluate(it) }
    }

    fun part1(input: List<String>): Int {
        val (initialDots, instructions) = parseInput(input)
        return fold(initialDots, instructions.first()).distinct().size
    }

    fun part2(input: List<String>): Set<Dot> {
        val (initialDots, instructions) = parseInput(input)

        return instructions
            .fold(initialDots, ::fold)
            .toSet()
    }

    val testInput = readInputLines("Day13_test")
    check(part1(testInput) == 17)

    val input = readInputLines("Day13")
    println("Part 1: " + part1(input))
    println("Part 2:")
    printDots(part2(input))
}

data class Dot(val x: Int, val y: Int)
enum class Axis { X, Y }
data class Instruction(val axis: Axis, val value: Int) {
    fun evaluate(dot: Dot) = when (axis) {
        Axis.X -> if (dot.x > value) dot.copy(x = dot.x - ((dot.x - value) * 2)) else dot
        Axis.Y -> if (dot.y > value) dot.copy(y = dot.y - ((dot.y - value) * 2)) else dot
    }
}
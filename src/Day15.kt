import java.util.*

fun main() {
    data class Dimensions(val width: Int, val height: Int) {
        operator fun contains(point: Pair<Int, Int>) = point.first in 0 until width && point.second in 0 until height
    }

    data class Vertex(val index: Int, val distance: Int): Comparable<Vertex> {
        override fun compareTo(other: Vertex) = compareValuesBy(this, other) { it.distance }
        fun getNeighbors(dimensions: Dimensions): Iterable<Int> {
            val x = index % dimensions.width
            val y = index / dimensions.width

            return listOf(x + 1 to y, x - 1 to y, x to y + 1, x to y - 1)
                .filter { it in dimensions }
                .map { it.first + it.second * dimensions.width }
        }
    }

    fun calculateDistancesFrom(start: Int, nodes: List<Int>, dimensions: Dimensions): IntArray {
        val distances = IntArray(nodes.size) { if (it == start) 0 else Int.MAX_VALUE }
        val queue = PriorityQueue<Vertex>().apply { add(Vertex(index = start, distance = 0)) }

        while (queue.isNotEmpty()) {
            val vertex = queue.poll()
            vertex.getNeighbors(dimensions)
                .filter { distances[vertex.index] + nodes[it] < distances[it] }
                .forEach {
                    val newDist = distances[vertex.index] + nodes[it]
                    distances[it] = newDist
                    queue.add(Vertex(it, newDist))
                }
        }
        return distances
    }

    fun part1(input: List<String>): Int {
        val dimensions = Dimensions(input.first().length, input.size)
        val nodes = input.flatMap { it.asIterable() }
            .map { it.digitToInt() }

        val distances = calculateDistancesFrom(0, nodes, dimensions)
        return distances.last()
    }

    fun part2(input: List<String>): Int {
        val dimensions = Dimensions(input.first().length * 5, input.size * 5)
        val expandedLines = input.map { line ->
            line.map { it.digitToInt() }
        }.flatMap { line ->
            (0..4).flatMap { offset ->
                line.map { if (it + offset > 9) (it + offset) % 9 else it + offset }
            }
        }

        val nodes = (0..4).flatMap { offset ->
            expandedLines.map { if (it + offset > 9) (it + offset) % 9 else it + offset }
        }

        val distances = calculateDistancesFrom(0, nodes, dimensions)
        return distances.last()
    }

    val testInput = readInputLines("Day15_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInputLines("Day15")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
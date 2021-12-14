fun main() {
    fun findAllPaths(dest: String, currPath: List<String>, visited: Set<String>, adjMap: Map<String, List<String>>): List<List<String>> {
        val currentNode = currPath.last()
        if (currentNode == dest) return listOf(currPath)

        val seen = visited + currentNode
        val neighbors = adjMap[currentNode] ?: throw Exception("Missing adjacency list for node '$currentNode'")

        return neighbors.filter { it != "start" }
            .filter { it.isUpperCase() || it !in seen }
            .flatMap { findAllPaths(dest, currPath + it, seen, adjMap)  }
    }

    fun findAllPaths(dest: String, currPath: List<String>, visitCounts: Map<String, Int>, adjMap: Map<String, List<String>>, singleVisitOnly: Boolean = true): List<List<String>> {
        val currentNode = currPath.last()
        if (currentNode == dest) return listOf(currPath)

        val updatedCounts = visitCounts + (currentNode to (visitCounts[currentNode]?.plus(1) ?: 1))
        val visitedTwice = updatedCounts.entries.any { !it.key.isUpperCase() && it.value > 1 }
        val neighbors = adjMap[currentNode] ?: throw Exception("Missing adjacency list for node '$currentNode'")

        return neighbors.filter { it != "start" }
            .filter { it.isUpperCase() || it !in updatedCounts || (singleVisitOnly && !visitedTwice) }
            .flatMap { findAllPaths(dest, currPath + it, updatedCounts, adjMap)  }
    }

    fun findAllPaths(src: String, dest: String, adjMap: Map<String, List<String>>): List<List<String>> {
        return findAllPaths(dest, listOf(src), setOf(), adjMap)
    }

    fun findAllPathsPart2(src: String, dest: String, adjMap: Map<String, List<String>>): List<List<String>> {
        return findAllPaths(dest, listOf(src), mapOf(), adjMap, singleVisitOnly = false)
    }

    fun parseInputToAdjacencyLists(input: List<String>): Map<String, List<String>> {
        val adjMap = input.asSequence()
            .map { it.split("-") }
            .flatMap { listOf(it, it.reversed()) }
            .map { it.first() to it.last() }
            .groupingBy { it.first }
            .aggregate { _, accumulator: List<String>?, element, first ->
                if (first) listOf(element.second) else accumulator?.plus(element.second)
            }
            .mapValues { it.value ?: listOf() }
        return adjMap
    }

    fun part1(input: List<String>): Int {
        val adjMap = parseInputToAdjacencyLists(input)

        val paths = findAllPaths("start", "end", adjMap)
        return paths.size
    }

    fun part2(input: List<String>): Int {
        val adjMap = parseInputToAdjacencyLists(input)

        val paths = findAllPathsPart2("start", "end", adjMap)
        return paths.size
    }

    val testInput1 = readInput("Day12_test1")
    val testInput2 = readInput("Day12_test2")
    val testInput3 = readInput("Day12_test3")

    check(part1(testInput1) == 10)
    check(part1(testInput2) == 19)
    check(part1(testInput3) == 226)

    check(part2(testInput1) == 36)
    check(part2(testInput2) == 103)
    check(part2(testInput3) == 3509)

    val input = readInput("Day12")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

fun String.isUpperCase(): Boolean = this.all { it.isUpperCase() }
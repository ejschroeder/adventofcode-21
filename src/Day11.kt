fun main() {
    data class Coord(val i: Int, val j: Int) {
        fun neighbors(): List<Coord> {
            return (-1..1).flatMap { di ->
                (-1..1).map { dj ->
                    Coord(i + di, j + dj)
                }
            }.filter { it != this }
        }

        fun isValid(grid: List<List<Int>>) = i in grid.indices && j in grid[i].indices
    }

    fun simulateStep(grid: List<MutableList<Int>>): Int {
        val queue = ArrayDeque<Coord>()
        for (i in grid.indices) {
            for (j in grid[i].indices) {
                grid[i][j]++
                if (grid[i][j] > 9) {
                    queue.addLast(Coord(i, j))
                }
            }
        }

        val flashes = HashSet<Coord>()
        while (queue.isNotEmpty()) {
            val coord = queue.removeFirst()

            if (coord in flashes) {
                continue
            }

            flashes.add(coord)
            for (neighbor in coord.neighbors()) {
                if (neighbor.isValid(grid)) {
                    grid[neighbor.i][neighbor.j] += 1
                    if (grid[neighbor.i][neighbor.j] > 9) {
                        queue.addLast(neighbor)
                    }
                }
            }
        }

        for (i in grid.indices) {
            for (j in grid[i].indices) {
                if (grid[i][j] > 9)
                    grid[i][j] = 0
            }
        }

        return flashes.size
    }

    fun part1(input: List<String>): Int {
        val grid = input.map { line -> line.asIterable().map { it.digitToInt() }.toMutableList() }
        var total = 0
        for (step in 1..100) {
            total += simulateStep(grid)
        }
        return total
    }

    fun part2(input: List<String>): Int {
        val grid = input.map { line -> line.map { it.digitToInt() }.toMutableList() }
        val totalOctopi = grid.size * grid.first().size
        
        var flashCount = 0
        var step = 0
        while (flashCount != totalOctopi) {
            flashCount = simulateStep(grid)
            step++
        }

        return step
    }

    val testInput = readInputLines("Day11_test")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInputLines("Day11")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}


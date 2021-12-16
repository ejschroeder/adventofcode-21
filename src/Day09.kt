fun getLowPoints(board: List<List<Int>>): Set<Location> {
    val locations = mutableSetOf<Location>()
    return board.flatMapIndexedTo(locations) { rowIdx, row ->
        row.mapIndexed { colIdx, value -> Location(rowIdx, colIdx, value) }
            .filter {
                val north = if (it.x - 1 in board.indices) board[it.x-1][it.y] else Int.MAX_VALUE
                val east = if (it.y + 1 in board[it.x].indices) board[it.x][it.y+1] else Int.MAX_VALUE
                val south = if (it.x + 1 in board.indices) board[it.x+1][it.y] else Int.MAX_VALUE
                val west = if (it.y - 1 in board[it.x].indices) board[it.x][it.y-1] else Int.MAX_VALUE

                it.value < north && it.value < east && it.value < south && it.value < west
            }
    }
}

fun getBasinLocations(location: Location, board: List<List<Int>>): Set<Location> {
    val queue = ArrayDeque(listOf(location.toCoord()))
    val seen = mutableSetOf<Location>()

    while (queue.isNotEmpty()) {
        val coord = queue.removeFirst()
        if (coord.isValid(board) && board[coord.x][coord.y] != 9) {
            val loc = Location(coord.x, coord.y, board[coord.x][coord.y])
            if (loc !in seen) {
                seen.add(loc)
                queue.addLast(Coord(coord.x + 1, coord.y))
                queue.addLast(Coord(coord.x, coord.y + 1))
                queue.addLast(Coord(coord.x - 1, coord.y))
                queue.addLast(Coord(coord.x, coord.y - 1))
            }
        }
    }

    return seen
}

fun main() {
    fun part1(input: List<String>): Int {
        val board = input.map { line -> line.map { it.digitToInt() } }

        return getLowPoints(board)
            .sumOf { it.value + 1 }
    }

    fun part2(input: List<String>): Int {
        val board = input.map { line -> line.map { it.digitToInt() } }

        return getLowPoints(board)
            .asSequence()
            .map { getBasinLocations(it, board).size }
            .sorted()
            .toList()
            .takeLast(3)
            .reduce { acc, i -> acc * i }
    }

    val testInput = readInputLines("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInputLines("Day09")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

data class Coord(val x: Int, val y: Int) {
    fun isValid(board: List<List<Int>>) = x in board.indices && y in board[x].indices
}
data class Location(val x: Int, val y: Int, val value: Int) {
    fun toCoord() = Coord(x, y)
}
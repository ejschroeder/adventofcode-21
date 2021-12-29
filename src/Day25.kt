fun main() {
    fun parseInput(input: List<String>) = input.map { it.toList() }

    fun stepForward(board: List<List<Char>>): Pair<List<List<Char>>, Int> {
        var moves = 0
        val rightBoard = MutableList(board.size) {
                i -> MutableList(board[i].size) { j ->
                    if (board[i][j] != '>') board[i][j] else '.'
                }
        }
        for (i in board.indices) {
            for (j in board[i].indices) {
                val neighborIndex = (j + 1) % board[i].size
                if (board[i][j] == '>') {
                    if (board[i][neighborIndex] == '.') {
                        moves++
                        rightBoard[i][neighborIndex] = '>'
                    } else {
                        rightBoard[i][j] = '>'
                    }
                }
            }
        }

        val downBoard = MutableList(rightBoard.size) { i ->
            MutableList(rightBoard[i].size) { j ->
                if (rightBoard[i][j] != 'v') rightBoard[i][j] else '.'
            }
        }
        for (i in rightBoard.indices) {
            val neighborIndex = (i + 1) % board.size
            for (j in rightBoard[i].indices) {
                if (rightBoard[i][j] == 'v') {
                    if (rightBoard[neighborIndex][j] == '.') {
                        moves++
                        downBoard[neighborIndex][j] = 'v'
                    } else {
                        downBoard[i][j] = 'v'
                    }
                }
            }
        }

        return downBoard to moves
    }

    fun part1(input: List<String>): Int {
        val initialState = parseInput(input)

        var state = initialState
        var steps = 0
        do {
            val result = stepForward(state)
            steps++
            state = result.first
        } while (result.second != 0)

        return steps
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInputLines("Day25_test")
    check(part1(testInput) == 58)
    check(part2(testInput) == 0)

    val input = readInputLines("Day25")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
fun main() {
    fun part1(input: List<String>): Int {
        val numbers = getBingoNumbers(input)
        var boards = getBingoBoards(input)

        for (number in numbers) {
            boards = boards.map { it.mark(number) }
            val winningBoard = boards.firstOrNull { it.isWin() }
            if (winningBoard != null) {
                return winningBoard.score * number
            }
        }

        return 0
    }

    fun part2(input: List<String>): Int {
        val numbers = getBingoNumbers(input)
        var boards = getBingoBoards(input)

        for (number in numbers) {
            boards = boards.map { it.mark(number) }

            if (boards.size == 1) {
                if (boards.first().isWin())
                    return boards.first().score * number
            } else {
                boards = boards.filter { !it.isWin() }
            }

        }

        return 0
    }

    val testInput = readInputLines("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInputLines("Day04")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

fun getBingoBoards(input: List<String>) = input.asSequence()
    .drop(1)
    .chunked(6)
    .map { it.drop(1) }
    .map { it.flatMap { boardRow -> boardRow.trim().split("\\s+".toRegex()) } }
    .map { it.map { num -> BingoCell(num.toInt()) } }
    .map { BingoBoard(it) }
    .toList()

fun getBingoNumbers(input: List<String>) = input.first().split(",").map { it.toInt() }

data class BingoCell(val value: Int, val checked: Boolean = false) {
    fun checked() = copy(checked = true)
}

data class BingoBoard(val cells: List<BingoCell>) {
    val score: Int
        get() = cells.filter { !it.checked }.sumOf { it.value }

    fun mark(number: Int) = BingoBoard(cells.map { if (it.value == number) it.checked() else it })

    fun isWin(): Boolean {
        return cells.chunked(5).any { row -> row.all { it.checked } }
                || cells.withIndex().groupBy { it.index % 5 }.values.any { col -> col.all { it.value.checked } }
    }
}

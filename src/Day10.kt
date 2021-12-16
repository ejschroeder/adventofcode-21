fun main() {
    fun part1(input: List<String>): Int {
        return input.map(::validateSubsystemLine)
            .filter { it.isFailure }
            .mapNotNull { it.exceptionOrNull() as? SyntaxException }
            .sumOf { getPart1PointValueForCharacter(it.unexpectedCharacter) }
    }

    fun part2(input: List<String>): Long {
        val points = input.asSequence()
            .map(::validateSubsystemLine)
            .filter { it.isSuccess }
            .mapNotNull { it.getOrNull() }
            .map { chars -> chars.fold(0L) { acc, c -> (acc * 5) + getPart2PointValueForCharacter(c) } }
            .sorted()
            .toList()

        return points[points.size / 2]
    }

    val testInput = readInputLines("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInputLines("Day10")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

fun getPart1PointValueForCharacter(char: Char) = when(char) {
    ')' -> 3
    ']' -> 57
    '}' -> 1197
    '>' -> 25137
    else -> 0
}

fun getPart2PointValueForCharacter(char: Char) = when(char) {
    '(' -> 1
    '[' -> 2
    '{' -> 3
    '<' -> 4
    else -> 0
}

fun validateSubsystemLine(line: String): Result<List<Char>> {
    val stack = ArrayDeque<Char>()
    for (token in line) {
        when {
            isOpeningToken(token) -> stack.addLast(token)
            getClosingToken(stack.last()) != token -> return Result.failure(SyntaxException(token))
            else -> stack.removeLast()
        }
    }
    return Result.success(stack.asReversed())
}

fun isOpeningToken(token: Char) = when(token) {
    '(', '[', '{', '<' -> true
    else -> false
}

fun getClosingToken(token: Char) = when(token) {
    '(' -> ')'
    '[' -> ']'
    '{' -> '}'
    '<' -> '>'
    else -> null
}

class SyntaxException(val unexpectedCharacter: Char): Exception()
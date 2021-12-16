data class Pos(val x: Int, val y: Int, val aim: Int)

fun main() {
    fun processCommand(currentPos: Pos, command: Pair<String, Int>): Pos {
        val (direction, amt) = command
        return when (direction) {
            "forward" -> currentPos.copy(x = currentPos.x + amt)
            "up" -> currentPos.copy(y = currentPos.y - amt)
            "down" -> currentPos.copy(y = currentPos.y + amt)
            else -> currentPos
        }
    }

    fun processCommandWithAim(currentPos: Pos, command: Pair<String, Int>): Pos {
        val (direction, amt) = command
        return when (direction) {
            "forward" -> currentPos.copy(x = currentPos.x + amt, y = currentPos.y + (currentPos.aim * amt))
            "up" -> currentPos.copy(aim = currentPos.aim - amt)
            "down" -> currentPos.copy(aim = currentPos.aim + amt)
            else -> currentPos
        }
    }

    fun part1(input: List<String>): Int {
        val finalPos = input.asSequence()
            .map { it.substringBefore(' ') to it.substringAfter(' ').toInt() }
            .fold(Pos(0, 0, 0), ::processCommand)

        return finalPos.x * finalPos.y
    }

    fun part2(input: List<String>): Int {
        val finalPos = input.asSequence()
            .map { it.substringBefore(' ') to it.substringAfter(' ').toInt() }
            .fold(Pos(0, 0, 0), ::processCommandWithAim)

        return finalPos.x * finalPos.y
    }

    val testInput = readInputLines("Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInputLines("Day02")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
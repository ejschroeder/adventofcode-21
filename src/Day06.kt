fun getNextState(state: List<Int>): List<Int> {
    val newCount = state.count { it == 0 }
    return state.map { if (it == 0) 6 else it - 1 } + List(newCount) { 8 }
}

val fishCountsAtTime = mutableMapOf<Int, Long>()

fun getPopulationFromNewFish(timeRemaining: Int): Long {
    var fishCount = 1L
    var currentTime = timeRemaining - 9

    while(currentTime > 0) {
        val childFishCount = fishCountsAtTime[currentTime] ?: getPopulationFromNewFish(currentTime)
        fishCountsAtTime[currentTime] = childFishCount
        fishCount += childFishCount
        currentTime -= 7
    }
    return fishCount
}

fun main() {
    fun part1(input: List<String>): Int {
        val startingState = input.first().split(",").map { it.toInt() }
        val finalState = (1..80).fold(startingState) { acc, _ -> getNextState(acc) }
        return finalState.count()
    }

    fun part2(input: List<String>): Long {
        val startingState = input.first().split(",").map { it.toInt() }
        return startingState.map { 256 + (9 - it) }.sumOf(::getPopulationFromNewFish)
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5934)
    check(part2(testInput) == 26984457539L)

    val input = readInput("Day06")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
fun main() {
    fun getMostCommonBitInPosition(pos: Int, binaryStrings: List<String>): Char {
        val freqSum = binaryStrings.fold(0) { acc, s ->
            if (s[pos] == '1') acc + 1 else acc - 1
        }

        return if (freqSum >= 0) '1' else '0'
    }

    fun part1(input: List<String>): Int {
        val wordLength = input.first().length

        var epsilon = ""
        var gamma = ""
        for (idx in 0 until wordLength) {
            val mostCommon = getMostCommonBitInPosition(idx, input)
            val leastCommon = if (mostCommon == '1') '0' else '1'

            epsilon += mostCommon
            gamma += leastCommon
        }

        return epsilon.toInt(2) * gamma.toInt(2)
    }

    fun part2(input: List<String>): Int {
        val wordLength = input.first().length
        var binaryNums = input
        for (idx in 0 until wordLength) {
            val mostCommon = getMostCommonBitInPosition(idx, binaryNums)
            binaryNums = binaryNums.filter { it[idx] == mostCommon }
            if (binaryNums.size == 1) break
        }
        val o2Rating = binaryNums.first().toInt(2)

        binaryNums = input
        for (idx in 0 until wordLength) {
            val leastCommon = if (getMostCommonBitInPosition(idx, binaryNums) == '1') '0' else '1'
            binaryNums = binaryNums.filter { it[idx] == leastCommon }
            if (binaryNums.size == 1) break
        }
        val co2Rating = binaryNums.first().toInt(2)

        return o2Rating * co2Rating
    }

    val testInput = readInputLines("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInputLines("Day03")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

import kotlin.math.max
import kotlin.math.min

enum class State { ON, OFF }

fun main() {
    data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
        val volume: Long
            get() = (xRange.last - xRange.first + 1L) * (yRange.last - yRange.first + 1L) * (zRange.last - zRange.first + 1L)
        fun intersects(other: Cuboid) = xRange.overlaps(other.xRange) && yRange.overlaps(other.yRange) && zRange.overlaps(other.zRange)
        fun overlap(other: Cuboid): Cuboid {
            val overlapXRange = max(xRange.first, other.xRange.first)..min(xRange.last, other.xRange.last)
            val overlapYRange = max(yRange.first, other.yRange.first)..min(yRange.last, other.yRange.last)
            val overlapZRange = max(zRange.first, other.zRange.first)..min(zRange.last, other.zRange.last)
            return Cuboid(overlapXRange, overlapYRange, overlapZRange)
        }
    }
    data class RebootStep(val state: State, val cuboid: Cuboid)

    fun parseInput(input: List<String>): List<RebootStep> {
        return input.map { it.split(" ") }
            .map { it[0] to it[1].split(",") }
            .map { pair -> pair.first to pair.second.map { it.substringAfter("=").split("..") } }
            .map { RebootStep(
                state = State.valueOf(it.first.uppercase()),
                cuboid = Cuboid(
                    xRange = it.second[0][0].toInt()..it.second[0][1].toInt(),
                    yRange = it.second[1][0].toInt()..it.second[1][1].toInt(),
                    zRange = it.second[2][0].toInt()..it.second[2][1].toInt()
                )
            ) }
    }

    fun generateSubSteps(steps: List<RebootStep>): List<RebootStep> {
        return steps.fold(listOf()) { subSteps, step ->
            val newSubSteps = subSteps.filter { it.cuboid.intersects(step.cuboid) }
                .map {
                    when (it.state) {
                        State.ON -> RebootStep(State.OFF, cuboid = step.cuboid.overlap(it.cuboid))
                        State.OFF -> RebootStep(State.ON, cuboid = step.cuboid.overlap(it.cuboid))
                    }
                }
            subSteps + newSubSteps + if (step.state == State.ON) listOf(step) else listOf()
        }
    }

    fun part1(input: List<String>): Long {
        val coreCuboid = Cuboid(-50..50, -50..50, -50..50)
        val steps = parseInput(input).filter { it.cuboid.intersects(coreCuboid) }

        val subSteps = generateSubSteps(steps)
        return subSteps.sumOf { if (it.state == State.ON) it.cuboid.volume else -it.cuboid.volume }
    }

    fun part2(input: List<String>): Long {
        val steps = parseInput(input)

        val subSteps = generateSubSteps(steps)
        return subSteps.sumOf { if (it.state == State.ON) it.cuboid.volume else -it.cuboid.volume }
    }

    val testInput = readInputLines("Day22_test")
    check(part1(testInput) == 590784L)
    val testInput2 = readInputLines("Day22_test2")
    check(part2(testInput2) == 2758514936282235L)

    val input = readInputLines("Day22")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

fun IntRange.overlaps(other: IntRange) = first <= other.last && last >= other.first
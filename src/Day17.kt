fun main() {
    data class Area(val xrange: IntProgression, val yrange: IntProgression)
    data class Velocity(val x: Int, val y: Int)

    fun maxHeightFromYVelocity(v: Int) = (v+1) * ((v+1) - 1) / 2

    fun yPosForVelocityAtTime(velocity: Int, time: Int) = velocity * time - (time * (time - 1) / 2)
    fun xPosForVelocityAtTime(velocity: Int, time: Int): Int {
        var vel = velocity
        var pos = 0
        var t = 1
        while (vel > 0 && t <= time) {
            pos += vel
            vel -= 1
            t += 1
        }
        return pos
    }

    fun getYIntersectionTimes(yVelocity: Int, area: Area): List<Int> {
        val times = mutableListOf<Int>()
        var position = 0
        var time = 0
        while (position >= area.yrange.first) {
            position = yPosForVelocityAtTime(yVelocity, time)
            if (position in area.yrange) times.add(time)
            time++
        }
        return times
    }

    fun parseInput(input: String): Area {
        val regex = "target area: x=(-?\\d+)\\.\\.(-?\\d+), y=(-?\\d+)\\.\\.(-?\\d+)".toRegex()
        return regex.matchEntire(input)?.destructured?.let {
                (x1, x2, y1, y2) -> Area(x1.toInt()..x2.toInt(), y1.toInt()..y2.toInt())
        } ?: throw IllegalStateException("Bad input '$input'")
    }

    fun getInitialVelocities(area: Area): List<Velocity> {
        val seq = sequence {
            var value = 0
            var count = 0
            while (true) {
                value += count
                count++
                yield(value)
            }
        }

        val minXVelocity = seq.withIndex()
            .first { it.value >= area.xrange.first }.index
        val maxXVelocity = area.xrange.last

        val yDistanceFromStart = 0 - area.yrange.first
        val maxYVelocity = yDistanceFromStart - 1
        val minYVelocity = area.yrange.first

        val velocities = mutableListOf<Velocity>()
        for (yVel in maxYVelocity downTo minYVelocity) {
            val times = getYIntersectionTimes(yVel, area)
            for (time in times) {
                for (xVel in minXVelocity..maxXVelocity) {
                    val xPos = xPosForVelocityAtTime(xVel, time)
                    if (xPos in area.xrange) {
                        velocities.add(Velocity(xVel, yVel))
                    }
                }
            }
        }
        return velocities
    }

    fun part1(input: String): Int {
        val area = parseInput(input)
        val initialVelocities = getInitialVelocities(area)
        return initialVelocities.map { maxHeightFromYVelocity(it.y) }.maxOf { it }
    }

    fun part2(input: String): Int {
        val area = parseInput(input)
        return getInitialVelocities(area).toSet().size
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 45)
    check(part2(testInput) == 112)

    val input = readInput("Day17")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
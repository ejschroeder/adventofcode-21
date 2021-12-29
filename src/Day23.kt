import java.util.*

enum class Area {
    HALL, ROOM1, ROOM2, ROOM3, ROOM4;

    val entrance: Int
        get() = when(this) {
            ROOM1 -> 2
            ROOM2 -> 4
            ROOM3 -> 6
            ROOM4 -> 8
            else -> throw Exception()
        }
}

enum class AmphipodType {
    A, B, C, D;

    val cost: Int
        get() = when(this) {
            A -> 1
            B -> 10
            C -> 100
            D -> 1000
        }

    val room: Area
        get() = when(this) {
            A -> Area.ROOM1
            B -> Area.ROOM2
            C -> Area.ROOM3
            D -> Area.ROOM4
        }

    fun isCorrectRoom(area: Area) = room == area
}

data class Amphipod(val type: AmphipodType, val area: Area, val position: Int) {
    fun inCorrectRoom() = type.isCorrectRoom(area)
}
/*
    [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
           0     0     0     0
           1     1     1     1
 */


fun main() {
    fun inHall(amphipod: Amphipod) = amphipod.area == Area.HALL

    fun getNextPositions(amphipod: Amphipod, state: Set<Amphipod>, roomSize: Int): List<Pair<Amphipod, Int>> {
        if (inHall(amphipod)) {
            val isRoomOccupied = state.filter { it.area == amphipod.type.room }.any { !it.inCorrectRoom() }
            if (isRoomOccupied) {
                return listOf()
            } else {
                val entrance = amphipod.type.room.entrance
                val hallPositions = state.filter { it.area == Area.HALL }
                    .map { it.position }
                    .filter { it != amphipod.position }
                val openRoomPosition = state.filter { it.area == amphipod.type.room }.minOfOrNull { it.position } ?: roomSize
                if (amphipod.position < entrance) {
                    val pathOpen = (amphipod.position..entrance).none { it in hallPositions }
                    return if (pathOpen) {
                        val moves = (entrance - amphipod.position) + openRoomPosition
                        listOf(amphipod.copy(position = openRoomPosition - 1, area = amphipod.type.room) to amphipod.type.cost * moves)
                    } else {
                        listOf()
                    }
                } else {
                    val pathOpen = (entrance..amphipod.position).none { it in hallPositions }
                    return if (pathOpen) {
                        val moves = (amphipod.position - entrance) + openRoomPosition
                        listOf(amphipod.copy(position = openRoomPosition - 1, area = amphipod.type.room) to amphipod.type.cost * moves)
                    } else {
                        listOf()
                    }
                }
            }
        } else { // in a room
            val otherTypesInRoom = state.filter { it.area == amphipod.area }.any { !it.inCorrectRoom() }
            if (!amphipod.inCorrectRoom() || otherTypesInRoom) {
                // need to move to hallway
                val roomClear = state.none { it.area == amphipod.area && it.position < amphipod.position }
                if (!roomClear) return listOf()

                val entrance = amphipod.area.entrance
                val hallPositions = state.filter { it.area == Area.HALL }.map { it.position }
                val leftHallPositions = (entrance - 1 downTo 0)
                    .filter { it != 2 && it != 4 && it != 6 && it != 8 }
                    .takeWhile { it !in hallPositions }
                    .map {
                        val steps = entrance - it + (amphipod.position) + 1
                        amphipod.copy(position = it, area = Area.HALL) to steps * amphipod.type.cost
                    }
                val rightHallPositions = (entrance + 1 until 11)
                    .filter { it != 2 && it != 4 && it != 6 && it != 8 }
                    .takeWhile { it !in hallPositions }
                    .map {
                        val steps = it - entrance + (amphipod.position) + 1
                        amphipod.copy(position = it, area = Area.HALL) to steps * amphipod.type.cost
                    }
                return leftHallPositions + rightHallPositions
            } else {
                return listOf()
            }
        }
    }

    fun getNextStatesWithCost(state: Set<Amphipod>, roomSize: Int): List<Pair<Set<Amphipod>, Int>> {
        return state.flatMap { amphipod ->
            getNextPositions(amphipod, state, roomSize).map { nextPos ->
                state.map { if (it == amphipod) nextPos.first else it }.toSet() to nextPos.second
            }
        }
    }

    fun getPositionFromIndex(value: IndexedValue<Char>): Pair<Area, Int> {
        val row = value.index / 13
        val area = when (value.index % 13) {
            3 -> Area.ROOM1
            5 -> Area.ROOM2
            7 -> Area.ROOM3
            9 -> Area.ROOM4
            else -> throw Exception()
        }
        return area to row - 2
    }

    fun parseInput(input: List<String>): Set<Amphipod> {
        val board = input.flatMap { it.toList() }
        val amphipods = board.withIndex()
            .filter { it.value.isLetter() }
            .groupBy { it.value }
            .flatMap { entry ->
                entry.value
                    .map(::getPositionFromIndex)
                    .map { Amphipod(AmphipodType.valueOf("${entry.key}"), it.first, it.second) }
            }.toSet()

        return amphipods
    }

    /*

        [0, 1, 2, 3, 4, 5, 6]
              0  0  0  0
              1  1  1  1
     */

    fun isSolved(state: Set<Amphipod>) = state.all { it.inCorrectRoom() }

    fun part1(input: List<String>): Int {
        val initialState = parseInput(input) to 0
        val costs = mutableMapOf(initialState)
        val queue = PriorityQueue<Pair<Set<Amphipod>, Int>>(compareBy { it.second })
        queue.add(initialState)

        while (queue.isNotEmpty()) {
            val (state, cost) = queue.poll()
            if (isSolved(state))
                return cost

            val nextStates = getNextStatesWithCost(state, roomSize = 2)
            for (nextState in nextStates) {
                val currentCost = nextState.second + cost
                val minCost = costs[nextState.first] ?: Int.MAX_VALUE
                if (currentCost < minCost) {
                    queue.add(nextState.first to currentCost)
                    costs[nextState.first] = currentCost
                }
            }
        }
        return -1
    }

    fun part2(input: List<String>): Int {
        val additionalAmphipods = listOf(
            "  #D#C#B#A#  ",
            "  #D#B#A#C#  "
        )

        val modifiedInput = input.take(3) + additionalAmphipods + input.takeLast(2)

        val initialState = parseInput(modifiedInput) to 0
        val costs = mutableMapOf(initialState)
        val queue = PriorityQueue<Pair<Set<Amphipod>, Int>>(compareBy { it.second })
        queue.add(initialState)

        while (queue.isNotEmpty()) {
            val (state, cost) = queue.poll()
            if (isSolved(state))
                return cost

            val nextStates = getNextStatesWithCost(state, roomSize = 4)
            for (nextState in nextStates) {
                val currentCost = nextState.second + cost
                val minCost = costs[nextState.first] ?: Int.MAX_VALUE
                if (currentCost < minCost) {
                    queue.add(nextState.first to currentCost)
                    costs[nextState.first] = currentCost
                }
            }
        }
        return -1
    }

    val testInput = readInputLines("Day23_test")
    check(part1(testInput) == 12521)
    check(part2(testInput) == 44169)

    val input = readInputLines("Day23")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
fun main() {
    data class Player(val id: Int, val position: Int, val score: Int = 0)
    data class QuantumGameState(val players: List<Player>, val playerTurn: Int = 0, val rollCount: Int = 0) {
        val isWon: Boolean
            get() = players.any { it.score >= 21 }

        val winningPlayer: Player?
            get() = players.firstOrNull { it.score >= 21 }

        fun rollNextTurn(roll: Int): QuantumGameState {
            val nextRollCount = (rollCount + 1) % 3
            val finalRoll = nextRollCount == 0
            val nextPlayer = if (finalRoll) (playerTurn + 1) % players.size else playerTurn
            val updatedPlayers = players.mapIndexed { index, player -> if (index == playerTurn) advancePlayer(player, roll, finalRoll = finalRoll) else player }
            return copy(players = updatedPlayers, playerTurn = nextPlayer, rollCount = (rollCount + 1) % 3)
        }

        private fun advancePlayer(player: Player, roll: Int, finalRoll: Boolean): Player {
            val nextPosition = (player.position - 1 + roll) % 10 + 1
            val score = if (finalRoll) player.score + nextPosition else player.score
            return player.copy(position = nextPosition, score = score)
        }
    }
    data class Game(val players: List<Player>, val die: Iterator<Int>, val turn: Int = 0) {
        val isWon: Boolean
            get() = players.any { it.score >= 1000 }

        fun nextTurn(): Game {
            val roll = die.next()
            val updatedPlayers = players.mapIndexed { index, player -> if (index == turn % players.size) advancePlayer(player, roll) else player }
            return copy(players = updatedPlayers, turn = turn + 1)
        }

        private fun advancePlayer(player: Player, roll: Int): Player {
            val nextPosition = (player.position - 1 + roll) % 10 + 1
            return player.copy(position = nextPosition, score = player.score + nextPosition)
        }
    }

    fun getDeterministicDie(): Iterator<Int> {
        return generateSequence(1) { if (it == 100) 1 else it + 1 }
            .chunked(3)
            .map { it.sum() }
            .iterator()
    }

    fun mergeMaps(mapA: Map<Int, Long>, mapB: Map<Int, Long>, mapC: Map<Int, Long>): Map<Int, Long> {
        return (mapA.asSequence() + mapB.asSequence() + mapC.asSequence())
            .groupBy({ it.key }, { it.value })
            .mapValues { (_, values) -> values.sum() }
    }

    fun countWinsFromState(game: QuantumGameState, cache: MutableMap<QuantumGameState, Map<Int, Long>> = mutableMapOf()): Map<Int, Long> {
        if (game.isWon) {
            val player = game.winningPlayer ?: throw Exception("Winning game but no winning player")
            return mapOf(player.id to 1L)
        }

        cache[game]?.let { return it }

        val playerWinsFromRoll1 = countWinsFromState(game.rollNextTurn(1), cache)
        val playerWinsFromRoll2 = countWinsFromState(game.rollNextTurn(2), cache)
        val playerWinsFromRoll3 = countWinsFromState(game.rollNextTurn(3), cache)

        val mergedCounts = mergeMaps(playerWinsFromRoll1, playerWinsFromRoll2, playerWinsFromRoll3)
        cache[game] = mergedCounts
        return mergedCounts
    }

    fun parseInput(input: List<String>): Game {
        val players = input.map { it.substringAfter("starting position: ").toInt() }
            .mapIndexed { index, startingPos -> Player(id = index, position = startingPos) }

        return Game(players, getDeterministicDie())
    }

    fun part1(input: List<String>): Int {
        val game = parseInput(input)
        val winningGame = generateSequence(game) { it.nextTurn() }.first { it.isWon }
        val losingPlayer = winningGame.players.minByOrNull { it.score } ?: throw Exception()
        return losingPlayer.score * winningGame.turn * 3
    }

    fun part2(input: List<String>): Long {
        val game = parseInput(input)
        val quantumGame = QuantumGameState(game.players)
        val winCounts = countWinsFromState(quantumGame)
        return winCounts.maxOf { it.value }
    }

    val testInput = readInputLines("Day21_test")
    check(part1(testInput) == 739785)
    check(part2(testInput) == 444356092776315L)

    val input = readInputLines("Day21")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
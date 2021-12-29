fun main() {
    data class Instruction(val op: String, val args: List<String>)
    data class SubAlu(val input: List<Int> = listOf(), val registers: Map<String, Int> = mapOf("w" to 0, "x" to 0, "y" to 0, "z" to 0)) {
        fun execute(instruction: Instruction): SubAlu {
            return when (instruction.op) {
                "inp" -> input(instruction.args)
                "add" -> add(instruction.args)
                "mul" -> mul(instruction.args)
                "div" -> div(instruction.args)
                "mod" -> mod(instruction.args)
                "eql" -> eql(instruction.args)
                else -> this
            }
        }

        private fun eql(args: List<String>): SubAlu {
            val result =  if (getValue(args[0]) == getValue(args[1])) 1 else 0
            return copy(registers = registers + (args[0] to result))
        }

        private fun mod(args: List<String>): SubAlu {
            val mod = getValue(args[0]) % getValue(args[1])
            return copy(registers = registers + (args[0] to mod))
        }

        private fun div(args: List<String>): SubAlu {
            val quot = getValue(args[0]) / getValue(args[1])
            return copy(registers = registers + (args[0] to quot))
        }

        private fun mul(args: List<String>): SubAlu {
            val product = getValue(args[0]) * getValue(args[1])
            return copy(registers = registers + (args[0] to product))
        }

        private fun add(args: List<String>): SubAlu {
            val sum = getValue(args[0]) + getValue(args[1])
            return copy(registers = registers + (args[0] to sum))
        }

        private fun input(args: List<String>): SubAlu {
            val inp = input.first()
            return copy(input = input.drop(1), registers = registers + (args[0] to inp))
        }

        private fun getValue(arg: String) = if (isRegister(arg)) registers[arg] ?: 0 else arg.toInt()

        private fun isRegister(arg: String) = when (arg) {
            "w", "x", "y", "z" -> true
            else -> false
        }
    }

    fun parseInput(input: List<String>): List<Instruction> {
        return input.map { it.split(" ") }
            .map { Instruction(it.first(), it.drop(1)) }
    }

    fun part1(input: List<String>): Int {
        val instructions = parseInput(input)
        val aluInput = "13579246899999".map { it.digitToInt() }.toList()
        val allStates = instructions.fold(listOf(SubAlu(input = aluInput))) { history, instruction -> history + history.last().execute(instruction) }
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInputLines("Day24_test")
    check(part1(testInput) == -1)
    check(part2(testInput) == 0)

    val input = readInputLines("Day24")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
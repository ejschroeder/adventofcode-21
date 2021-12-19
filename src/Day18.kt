import kotlin.math.ceil
import kotlin.math.floor

class SnailfishNumber(root: SnailfishNode.Pair) {
    private val root: SnailfishNode.Pair

    init {
        this.root = reduce(root)
    }

    fun magnitude() = root.magnitude()

    companion object {
        fun fromString(string: String): SnailfishNumber {
            val root = parsePair(string)
            return SnailfishNumber(root)
        }

        private fun parsePair(string: String): SnailfishNode.Pair {
            val pair = string.removeSurrounding(prefix = "[", suffix = "]")

            val commaLocation = if (pair.startsWith('[')) findIndexOfClosingBrace(pair) + 1 else 1

            val leftString = pair.substring(0 until commaLocation)
            val rightString = pair.substring(commaLocation + 1)

            val leftItem = if (leftString.startsWith("[")) parsePair(leftString) else SnailfishNode.Number(leftString.toInt())
            val rightItem = if (rightString.startsWith("[")) parsePair(rightString) else SnailfishNode.Number(rightString.toInt())

            return SnailfishNode.Pair(leftItem, rightItem)
        }

        private fun findIndexOfClosingBrace(string: String): Int {
            if (string.first() != '[') throw Exception("Starting index is not index of opening brace")

            var count = 1
            var idx = 0
            do {
                idx++
                when (string[idx]) {
                    '[' -> count++
                    ']' -> count--
                }
            } while (count != 0 && idx < string.length)
            return if (count == 0) idx else throw Exception("Closing brace not found")
        }

        private fun explode(root: SnailfishNode.Pair, pair: SnailfishNode.Pair): SnailfishNode.Pair {
            val inorder = root.inorder()
            val replacements = mutableMapOf<SnailfishNode, SnailfishNode>(pair to SnailfishNode.Number(0))

            val explodingLeftNumber = pair.left as SnailfishNode.Number
            val leftIndex = inorder.indexOf(explodingLeftNumber) - 1
            if (leftIndex in inorder.indices) {
                val number = inorder[leftIndex]
                replacements += number to SnailfishNode.Number(number.value + explodingLeftNumber.value)
            }

            val explodingRightNumber = pair.right as SnailfishNode.Number
            val rightIndex = inorder.indexOf(explodingRightNumber) + 1
            if (rightIndex in inorder.indices) {
                val number = inorder[rightIndex]
                replacements += number to SnailfishNode.Number(number.value + explodingRightNumber.value)
            }

            return root.replace(replacements) as SnailfishNode.Pair
        }

        private fun split(root: SnailfishNode.Pair, number: SnailfishNode.Number): SnailfishNode.Pair {
            val half = number.value / 2.0
            val newPair = SnailfishNode.Pair(
                left = SnailfishNode.Number(floor(half).toInt()),
                right = SnailfishNode.Number(ceil(half).toInt())
            )
            return root.replace(number, newPair) as SnailfishNode.Pair
        }

        private fun reduce(root: SnailfishNode.Pair): SnailfishNode.Pair {
            var reductionFound = true
            var r = root
            while(reductionFound) {
                val explodingPair = r.inorderSearch { _, depth -> depth >= 4 }
                if (explodingPair != null) {
                    r = explode(r, explodingPair)
                    continue
                }

                val splittingPair = r.inorderSearch { pair, _ ->
                    pair.left is SnailfishNode.Number && pair.left.value >= 10 ||
                            pair.right is SnailfishNode.Number && pair.right.value >= 10
                }
                if (splittingPair != null) {
                    val number = if ((splittingPair.left is SnailfishNode.Number) && splittingPair.left.value >= 10 )
                        splittingPair.left
                    else
                        splittingPair.right as SnailfishNode.Number
                    r = split(r, number)
                    continue
                }

                reductionFound = false
            }
            return r
        }
    }

    operator fun plus(other: SnailfishNumber): SnailfishNumber {
        val newRoot = SnailfishNode.Pair(this.root, other.root)
        return SnailfishNumber(newRoot)
    }


    sealed class SnailfishNode {
        abstract fun magnitude(): Int
        abstract fun inorder(): List<Number>
        abstract fun replace(target: SnailfishNode, replacement: SnailfishNode): SnailfishNode
        abstract fun replace(targets: Map<SnailfishNode, SnailfishNode>): SnailfishNode

        class Number(val value: Int) : SnailfishNode() {
            override fun magnitude() = value
            override fun inorder() = listOf(this)
            override fun replace(target: SnailfishNode, replacement: SnailfishNode) = if (target == this) replacement else Number(this.value)
            override fun replace(targets: Map<SnailfishNode, SnailfishNode>) = if (this in targets) targets[this]!! else Number(this.value)
        }
        class Pair(val left: SnailfishNode, val right: SnailfishNode) : SnailfishNode() {
            override fun magnitude() = (left.magnitude() * 3) + (right.magnitude() * 2)
            override fun inorder() = left.inorder() + right.inorder()

            override fun replace(target: SnailfishNode, replacement: SnailfishNode): SnailfishNode {
                if (this == target) return replacement
                return Pair(left.replace(target, replacement), right.replace(target, replacement))
            }

            override fun replace(targets: Map<SnailfishNode, SnailfishNode>): SnailfishNode {
                if (this in targets) return targets[this]!!
                return Pair(left.replace(targets), right.replace(targets))
            }

            fun inorderSearch(depth: Int = 0, predicate: (pair: Pair, depth: Int) -> Boolean): Pair? {
                val leftTreeResult = if (left is Pair) left.inorderSearch(depth + 1, predicate) else null
                leftTreeResult?.let { return leftTreeResult }

                val found = predicate.invoke(this, depth)
                if (found) return this

                return if (right is Pair) right.inorderSearch(depth + 1, predicate) else null
            }
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val finalResult = input.map(SnailfishNumber::fromString)
            .reduce { acc, pair -> acc + pair }

        return finalResult.magnitude()
    }

    fun part2(input: List<String>): Int {
        val maxMagnitude = input.elementPairs()
            .flatMap { listOf(
                SnailfishNumber.fromString(it.first) to SnailfishNumber.fromString(it.second),
                SnailfishNumber.fromString(it.second) to SnailfishNumber.fromString(it.first)
            )}
            .map { it.first + it.second }
            .maxOf { it.magnitude() }

        return maxMagnitude
    }

    val testInput = readInputLines("Day18_test")
    check(part1(testInput) == 4140)
    check(part2(testInput) == 3993)

    val input = readInputLines("Day18")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

fun <T> List<T>.elementPairs(): Sequence<Pair<T, T>> = sequence {
    for(i in 0 until size-1)
        for(j in i+1 until size)
            yield(get(i) to get(j))
}
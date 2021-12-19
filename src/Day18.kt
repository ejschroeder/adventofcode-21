import kotlin.math.ceil
import kotlin.math.floor

sealed class SnailfishNumber {
    class Number(var value: Int) : SnailfishNumber()
    class Pair(var left: SnailfishNumber, var right: SnailfishNumber) : SnailfishNumber() {
        fun magnitude(): Int {
            val l = left
            val r = right

            val leftMagnitude = if (l is Pair) l.magnitude() else (l as Number).value
            val rightMagnitude = if (r is Pair) r.magnitude() else (r as Number).value
            return (leftMagnitude * 3) + (rightMagnitude * 2)
        }

        fun parentOf(snailfishNumber: SnailfishNumber): Pair? {
            if (left == snailfishNumber || right == snailfishNumber) return this

            val l = left
            if (l is Pair) {
                val leftResult = l.parentOf(snailfishNumber)
                leftResult?.let { return it }
            }

            val r = right
            if (r is Pair) {
                val rightResult = r.parentOf(snailfishNumber)
                rightResult?.let { return it }
            }

            return null
        }

        fun inorder(): List<Number> {
            val l = left
            val r = right
            val leftInorder = if (l is Pair) l.inorder() else listOf(l as Number)
            val rightInorder = if (r is Pair) r.inorder() else listOf(r as Number)
            return leftInorder + rightInorder
        }

        fun inorderSearch(depth: Int = 0, predicate: (pair: Pair, depth: Int) -> Boolean, ): Pair? {
            val l = left
            val leftTreeResult = if (l is Pair) l.inorderSearch(depth + 1, predicate) else null
            leftTreeResult?.let { return leftTreeResult }

            val found = predicate.invoke(this, depth)
            if (found) return this

            val r = right
            return if (r is Pair) r.inorderSearch(depth + 1, predicate) else null
        }

        fun explode(root: Pair) {
            val explodingLeftNumber = this.left as Number
            val explodingRightNumber = this.right as Number
            val inorder = root.inorder()
            val leftIndex = inorder.indexOf(explodingLeftNumber)
            val rightIndex = inorder.indexOf(explodingRightNumber)
            if (leftIndex - 1 in inorder.indices) {
                inorder[leftIndex - 1].value += explodingLeftNumber.value
            }
            if (rightIndex + 1 in inorder.indices) {
                inorder[rightIndex + 1].value += explodingRightNumber.value
            }
            val parent = root.parentOf(this)
            if (parent != null) {
                if (parent.left == this) {
                    parent.left = Number(0)
                } else {
                    parent.right = Number(0)
                }
            }
        }

        fun split() {
            if (this.left is Number && (this.left as Number).value >= 10) {
                val num = this.left as Number
                val half = num.value / 2.0
                this.left = Pair(Number(floor(half).toInt()), Number(ceil(half).toInt()))
            } else if (this.right is Number && (this.right as Number).value >= 10) {
                val num = this.right as Number
                val half = num.value / 2.0
                this.right = Pair(Number(floor(half).toInt()), Number(ceil(half).toInt()))
            }
        }

        fun reduce(): Pair {
            var reductionFound = true

            while(reductionFound) {
                val explodingPair = this.inorderSearch { _, depth -> depth >= 4 }
                if (explodingPair != null) {
                    explodingPair.explode(this)
                    continue
                }

                val splittingPair = this.inorderSearch { pair, _ ->
                    pair.left is Number && (pair.left as Number).value >= 10 ||
                            pair.right is Number && (pair.right as Number).value >= 10
                }
                if (splittingPair != null) {
                    splittingPair.split()
                    continue
                }

                reductionFound = false
            }

            return this
        }

        operator fun plus(other: Pair): Pair {
            return Pair(this, other).reduce()
        }
    }
}

fun main() {
    fun findIndexOfClosingBrace(string: String): Int {
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

    fun parsePair(string: String): SnailfishNumber.Pair {
        val pair = string.removeSurrounding(prefix = "[", suffix = "]")

        val commaLocation = if (pair.startsWith('[')) findIndexOfClosingBrace(pair) + 1 else 1

        val leftString = pair.substring(0 until commaLocation)
        val rightString = pair.substring(commaLocation + 1)

        val leftItem = if (leftString.startsWith("[")) parsePair(leftString) else SnailfishNumber.Number(leftString.toInt())
        val rightItem = if (rightString.startsWith("[")) parsePair(rightString) else SnailfishNumber.Number(rightString.toInt())

        return SnailfishNumber.Pair(leftItem, rightItem)
    }

    fun part1(input: List<String>): Int {
        val finalResult = input.map(::parsePair)
            .reduce { acc, pair -> acc + pair }

        return finalResult.magnitude()
    }

    fun part2(input: List<String>): Int {
        val maxMagnitude = input.elementPairs()
            .flatMap { listOf(
                parsePair(it.first) to parsePair(it.second),
                parsePair(it.second) to parsePair(it.first)
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
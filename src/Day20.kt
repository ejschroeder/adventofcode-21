
fun main() {
    data class Index2D(val x: Int, val y: Int) {
        operator fun minus(scalar: Int) = Index2D(x - scalar, y - scalar)
    }
    data class Image(val pixels: List<List<Char>>, val infiniteChar: Char) {
        fun pixelAt(index: Index2D) = if (index in this) pixels[index.x][index.y] else infiniteChar
        operator fun contains(index: Index2D) = index.x in pixels.indices && index.y in pixels[index.x].indices
    }

    fun parseInput(input: List<String>): Pair<List<Char>, Image> {
        val key = input.first().toList()
        val image = input.drop(2)
        val pixels = image.map { it.toList() }
        return key to Image(pixels, infiniteChar = '.')
    }

    fun getSurroundingPoints(index: Index2D): List<Index2D> {
        return (-1..1).flatMap { xOffset ->
            (-1..1).map { yOffset ->
                Index2D(index.x + xOffset, index.y + yOffset)
            }
        }
    }

    fun process(image: Image, lookup: List<Char>): Image {
        val processedPixels = List(image.pixels.size + 2) { xIdx ->
            List(image.pixels.first().size + 2) { yIdx ->
                val lookupIndex = getSurroundingPoints(Index2D(xIdx, yIdx))
                    .map { it - 1 }
                    .map(image::pixelAt)
                    .joinToString("") { if (it == '#') "1" else "0" }.toInt(2)

                lookup[lookupIndex]
            }
        }

        val nextInfiniteChar = when (image.infiniteChar) {
            '.' -> lookup[0]
            '#' -> lookup[511]
            else -> throw Exception("Invalid infinite char '${image.infiniteChar}'")
        }

        return Image(processedPixels, nextInfiniteChar)
    }

    fun part1(input: List<String>): Int {
        val (key, image) = parseInput(input)
        val finalImage = (1..2).fold(image) { acc, _ -> process(acc, key) }
        return finalImage.pixels.sumOf { row -> row.count { it == '#' } }
    }

    fun part2(input: List<String>): Int {
        val (key, image) = parseInput(input)
        val finalImage = (1..50).fold(image) { acc, _ -> process(acc, key) }
        return finalImage.pixels.sumOf { row -> row.count { it == '#' } }
    }

    val testInput = readInputLines("Day20_test")
    check(part1(testInput) == 35)
    check(part2(testInput) == 3351)

    val input = readInputLines("Day20")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
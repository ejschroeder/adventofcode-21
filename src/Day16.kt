abstract class BITSPacket(val version: Int, val type: Int) {
    abstract fun value(): Long
}

class LiteralPacket(version: Int, private val literalValue: Long): BITSPacket(version, 4) {
    override fun value() = literalValue

    override fun toString(): String {
        return "LiteralPacket(version=$version, type=$type, value=$literalValue)"
    }
}

class OperatorPacket(version: Int, type: Int, val packets: List<BITSPacket>): BITSPacket(version, type) {
    override fun value(): Long = when (type) {
        0 -> packets.sumOf { it.value() }
        1 -> packets.map { it.value() }.reduce { acc, value -> acc * value }
        2 -> packets.minOf { it.value() }
        3 -> packets.maxOf { it.value() }
        5 -> if (packets.first().value() > packets.last().value()) 1 else 0
        6 -> if (packets.first().value() < packets.last().value()) 1 else 0
        7 -> if (packets.first().value() == packets.last().value()) 1 else 0
        else -> throw Exception("Invalid packet type '$type'")
    }

    override fun toString(): String {
        return "OperatorPacket(version=$version, type=$type, packets=$packets)"
    }
}

fun parseLiteralPacket(packet: String, startIndex: Int): Pair<LiteralPacket, Int> {
    val packetStart = packet.drop(startIndex)
    val version = packetStart.take(3).toInt(2)
    var contents = packetStart.drop(6)
    var done = false
    var literal = ""
    var packetLength = 6
    while (!done) {
        val bitGroup = contents.take(5)
        literal += bitGroup.takeLast(4)
        packetLength += 5
        contents = contents.drop(5)
        if (bitGroup.startsWith("0")) {
            done = true
        }
    }
    return LiteralPacket(version, literal.toLong(2)) to startIndex + packetLength
}

fun parseOperatorPacket(packetString: String, startIndex: Int): Pair<OperatorPacket, Int> {
    val packetStart = packetString.drop(startIndex)
    val version = packetStart.take(3).toInt(2)
    val type = packetStart.drop(3).take(3).toInt(2)
    val lengthType = packetStart.drop(6).take(1)
    val contents = packetStart.drop(7)

    val packets: MutableList<BITSPacket> = mutableListOf()
    val totalLength: Int
    when(lengthType) {
        "0" -> {
            val length = contents.take(15).toInt(2)
            val subPacketStartIndex = startIndex + 7 + 15
            totalLength = length + 7 + 15

            var nextPacketStartIndex = subPacketStartIndex
            do {
                val (packet, packetEndIndex) = parsePacket(packetString, nextPacketStartIndex)
                nextPacketStartIndex = packetEndIndex
                packets.add(packet)
            } while (packetEndIndex - subPacketStartIndex < length)
        }
        "1" -> {
            val totalPacketCount = contents.take(11).toInt(2)
            var packetCount = 0
            var nextPacketStartIndex = startIndex + 7 + 11
            while (packetCount < totalPacketCount) {
                val (packet, endIndex) = parsePacket(packetString, nextPacketStartIndex)
                nextPacketStartIndex = endIndex
                packets.add(packet)
                packetCount++
            }
            totalLength = nextPacketStartIndex - startIndex
        }
        else -> throw Exception()
    }

    return OperatorPacket(version, type, packets) to startIndex + totalLength
}

fun parsePacket(packet: String, startIndex: Int): Pair<BITSPacket, Int> {
    val subPacket = packet.drop(startIndex)
    val type = subPacket.drop(3).take(3).toInt(2)

    return when (type) {
        4 -> parseLiteralPacket(packet, startIndex)
        else -> parseOperatorPacket(packet, startIndex)
    }
}

fun parsePacket(packet: String) = parsePacket(packet, 0).first

fun sumVersions(packet: BITSPacket): Int = when (packet) {
    is LiteralPacket -> packet.version
    is OperatorPacket -> packet.version + packet.packets.sumOf { sumVersions(it) }
    else -> 0
}

fun main() {
    fun part1(input: String): Int {
        val binaryString = input.flatMap { it.hexToBinaryString().asIterable() }.joinToString("")
        val packet = parsePacket(binaryString)
        return sumVersions(packet)
    }

    fun part2(input: String): Long {
        val binaryString = input.flatMap { it.hexToBinaryString().asIterable() }.joinToString("")
        val packet = parsePacket(binaryString)
        return packet.value()
    }

    check(part1(readInput("Day16_test1")) == 16)
    check(part1(readInput("Day16_test2")) == 12)
    check(part1(readInput("Day16_test3")) == 23)
    check(part1(readInput("Day16_test4")) == 31)

    val input = readInput("Day16")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

fun Char.hexToBinaryString() = when(this) {
    '0' -> "0000"
    '1' -> "0001"
    '2' -> "0010"
    '3' -> "0011"
    '4' -> "0100"
    '5' -> "0101"
    '6' -> "0110"
    '7' -> "0111"
    '8' -> "1000"
    '9' -> "1001"
    'A' -> "1010"
    'B' -> "1011"
    'C' -> "1100"
    'D' -> "1101"
    'E' -> "1110"
    'F' -> "1111"
    else -> throw IllegalArgumentException("Character '$this' is not a valid hex digit")
}
import kotlin.math.abs

fun main() {
    fun parseScanner(scannerInput: List<String>): Scanner {
        val scannerNumber = scannerInput.first()
            .substringAfter("--- scanner ")
            .dropLast(4)
            .toInt()
        val beacons = scannerInput.drop(1)
            .map { it.split(",") }
            .map { Point3D(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
            .toSet()

        return Scanner(scannerNumber, Point3D(0, 0, 0), beacons)
    }

    fun parseInput(input: List<String>): List<Scanner> {
        var remainingInput = input
        val scanners = mutableListOf<Scanner>()
        while (remainingInput.isNotEmpty()) {
            val blankLine = remainingInput.indexOfFirst { it == "" }.let { if (it == -1) remainingInput.size else it }
            val scannerSection = remainingInput.take(blankLine)
            scanners.add(parseScanner(scannerSection))
            remainingInput = remainingInput.drop(blankLine + 1)
        }
        return scanners
    }

    fun findOverlappingScannerWithOffsetApplied(scanners: List<Scanner>, solvedBeacons: Set<Point3D>): Scanner? {
        val solvedBeaconPairs = solvedBeacons.elementPairs()
        for (scanner in scanners) {
            for (orientation in scanner.orientations()) {
                for (beaconPair in orientation.beacons.elementPairs()) {
                    val combinedBeaconMatch = solvedBeaconPairs
                        .firstOrNull { it.first - it.second == beaconPair.first - beaconPair.second }
                    if (combinedBeaconMatch != null) {
                        val relativeOffset = combinedBeaconMatch.first - beaconPair.first
                        val offsetBeacons = orientation.beacons.map { it + relativeOffset }
                        if (offsetBeacons.count { it in solvedBeacons } >= 12) {
                            return scanner.copy(
                                location = scanner.location + relativeOffset,
                                beacons = offsetBeacons.toSet()
                            )
                        }
                    }
                }
            }
        }
        return null
    }

    fun solveScanners(scanners: List<Scanner>): List<Scanner> {
        val rootScanner = scanners.first()
        val solvedBeacons = rootScanner.beacons.toMutableSet()
        var remainingScanners = scanners.drop(1)
        val solvedScanners = mutableListOf(rootScanner)

        while (remainingScanners.isNotEmpty()) {
            val overlappingScanner = findOverlappingScannerWithOffsetApplied(remainingScanners, solvedBeacons)
                ?: throw Exception("Not able to find an overlapping scanner!")
            solvedScanners.add(overlappingScanner)
            solvedBeacons.addAll(overlappingScanner.beacons)
            remainingScanners = remainingScanners.filter { it.number != overlappingScanner.number }
        }
        return solvedScanners
    }

    fun part1(input: List<String>): Int {
        val scanners = parseInput(input)

        val solvedScanners = solveScanners(scanners)

        return solvedScanners.flatMap { it.beacons }.toSet().size
    }

    fun part2(input: List<String>): Int {
        val scanners = parseInput(input)
        val solvedScanners = solveScanners(scanners)
        return solvedScanners.elementPairs().maxOf { it.first.location.manhattanDistance(it.second.location) }
    }

    val testInput = readInputLines("Day19_test")
    check(part1(testInput) == 79)
    check(part2(testInput) == 3621)

    val input = readInputLines("Day19")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

data class Point3D(val x: Int, val y: Int, val z: Int): Comparable<Point3D> {
    operator fun minus(other: Point3D) = Vector(x - other.x, y - other.y, z - other.z)
    operator fun plus(vector: Vector) = Point3D(x + vector.x, y + vector.y, z + vector.z)

    fun manhattanDistance(other: Point3D) = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)

    fun rotate(matrix: Matrix3D): Point3D {
        return Point3D(
            x = (matrix.a11 * x) + (matrix.a12 * y) + (matrix.a13 * z),
            y = (matrix.a21 * x) + (matrix.a22 * y) + (matrix.a23 * z),
            z = (matrix.a31 * x) + (matrix.a32 * y) + (matrix.a33 * z)
        )
    }

    override fun compareTo(other: Point3D) = compareValuesBy(this, other, { it.x }, { it.y }, { it.z })
}

data class Scanner(val number: Int, val location: Point3D, val beacons: Set<Point3D>) {
    fun orientations(): Sequence<Scanner> {
        return sequence {
            for (orientation in ALL_ORIENTATIONS) {
                val rotatedBeacons = beacons.map { it.rotate(orientation) }.toSet()
                yield(copy(beacons = rotatedBeacons))
            }
        }
    }

    companion object {
        val ZERO_ROTATION = Matrix3D(
            1, 0, 0,
            0, 1, 0,
            0, 0, 1
        )

        val X_90 = Matrix3D(
            1, 0,0,
            0, 0,-1,
            0, 1,0
        )
        val X_180 = X_90 * X_90
        val X_270 = X_180 * X_90

        val Y_90 = Matrix3D(
            0,0,1,
            0,1,0,
            -1,0,0
        )
        val Y_180 = Y_90 * Y_90
        val Y_270 = Y_180 * Y_90

        val Z_90 = Matrix3D(
            0,-1, 0,
            1, 0, 0,
            0, 0, 1
        )
        val Z_180 = Z_90 * Z_90
        val Z_270 = Z_180 * Z_90

        val ALL_ORIENTATIONS = listOf(
            ZERO_ROTATION,
            Z_90,
            Z_180,
            Z_270,
            Y_90,
            Y_90 * Z_90,
            Y_90 * Z_180,
            Y_90 * Z_270,
            Y_180,
            Y_180 * Z_90,
            Y_180 * Z_180,
            Y_180 * Z_270,
            Y_270,
            Y_270 * Z_90,
            Y_270 * Z_180,
            Y_270 * Z_270,
            X_90,
            X_90 * Z_90,
            X_90 * Z_180,
            X_90 * Z_270,
            X_270,
            X_270 * Z_90,
            X_270 * Z_180,
            X_270 * Z_270,
        )
    }
}

data class Vector(val x: Int, val y: Int, val z: Int)

data class Matrix3D(
    val a11: Int,
    val a12: Int,
    val a13: Int,
    val a21: Int,
    val a22: Int,
    val a23: Int,
    val a31: Int,
    val a32: Int,
    val a33: Int,
) {
    operator fun times(other: Matrix3D): Matrix3D {
        return Matrix3D(
            a11 * other.a11 + a12 * other.a21 + a13 * other.a31,
            a11 * other.a12 + a12 * other.a22 + a13 * other.a32,
            a11 * other.a13 + a12 * other.a23 + a13 * other.a33,
            a21 * other.a11 + a22 * other.a21 + a23 * other.a31,
            a21 * other.a12 + a22 * other.a22 + a23 * other.a32,
            a21 * other.a13 + a22 * other.a23 + a23 * other.a33,
            a31 * other.a11 + a32 * other.a21 + a33 * other.a31,
            a31 * other.a12 + a32 * other.a22 + a33 * other.a32,
            a31 * other.a13 + a32 * other.a23 + a33 * other.a33,
        )
    }
}
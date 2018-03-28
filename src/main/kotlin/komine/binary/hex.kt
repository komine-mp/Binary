/*
 *  _  __               _                  __  __ ____
 * | |/ /___  _ __ ___ (_)_ __   ___      |  \/  |  _ \
 * | ' // _ \| '_ ` _ \| | '_ \ / _ \_____| |\/| | |_) |
 * | . \ (_) | | | | | | | | | |  __/_____| |  | |  __/
 * |_|\_\___/|_| |_| |_|_|_| |_|\___|     |_|  |_|_|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Komine Team
 * @link https://github.com/komine-mp/
 *
 */

package komine.binary

import kotlin.math.min

private val DUMP_WIDTH = 16
private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

/**
 * Array of bytes where i-th bit of the array means printability of char with i char-code
 */
// @formatter:off
private val PRINTABLE = intArrayOf(0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 127, 255, 255, 255, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
// @formatter:on

fun Byte.isPrintable() = toInt().let { ((PRINTABLE[it / 8] shl (it % 8)) and 1 == 1) }
fun Char.isPrintable() = (toInt() in 0..255) and toByte().isPrintable()

fun String.hex(): ByteArray {
    val len = length
    val data = ByteArray(len / 2)

    for (i in 0 until len step 2) {
        data[i / 2] =
            ((Character.digit(this[i], 16) shl 4) or Character.digit(this[i + 1], 16)).toByte()
    }

    return data
}

fun ByteArray.hexDump(
    offset: Int = 0,
    length: Int = size,
    startFrom: Int = offset,
    labelOffset: Int? = null,
    label: Char = '>'
) = buildString {
    val lines = (length + DUMP_WIDTH - 1) / DUMP_WIDTH
    val leftPad = (lines * DUMP_WIDTH + 1).toString().length

    var position = startFrom
    for (i in 0 until lines) {
        val positionString = position.toString(16)
        append(" ".repeat(leftPad - positionString.length))
        append(positionString)
        append(":")

        val from = position
        val to = min(position + DUMP_WIDTH, offset + length)
        position += DUMP_WIDTH

        for (j in from until to) {
            val byte = this@hexDump[j]
            val p1 = (byte.toInt() shr 4) and 0xF
            val p2 = (byte.toInt() shr 0) and 0xF

            append(if (labelOffset == j) label else ' ')
            append(HEX_CHARS[p1])
            append(HEX_CHARS[p2])
        }

        for (j in 0 until DUMP_WIDTH - (to - from)) append("   ")

        append(if (labelOffset == to && labelOffset == offset + length) label else " ")
        append("   ")

        for (j in from until to) {
            val byte = this@hexDump[j]

            if (byte.isPrintable()) append(byte.toChar())
            else append('.')
        }

        append('\n')
    }
}

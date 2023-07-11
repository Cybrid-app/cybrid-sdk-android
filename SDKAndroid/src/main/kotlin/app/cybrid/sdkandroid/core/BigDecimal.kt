package app.cybrid.sdkandroid.core

import java.math.MathContext
import java.math.RoundingMode
import java.text.NumberFormat
import java.math.BigDecimal as JavaBigDecimal

class BigDecimal(internal val value:JavaBigDecimal) : Comparable<BigDecimal> {

    constructor(value: Int) : this(JavaBigDecimal(value.toLong()))

    constructor(value: Long) : this(JavaBigDecimal(value))

    constructor(value: Double) : this(JavaBigDecimal(value))

    constructor(value: String) : this(JavaBigDecimal(value))

    operator fun plus(augend: BigDecimal): BigDecimal {
        return BigDecimal(value.add(augend.value))
    }

    operator fun minus(subtrahend: BigDecimal): BigDecimal {
        return BigDecimal(value.subtract(subtrahend.value))
    }

    operator fun times(multiplicand: BigDecimal): BigDecimal {
        return BigDecimal(value.multiply(multiplicand.value))
    }

    operator fun div(divisor: BigDecimal): BigDecimal {
        return BigDecimal(value.divide(divisor.value, MathContext.UNLIMITED))
    }

    fun divL(divisor: BigDecimal): BigDecimal {
        return BigDecimal(value.divide(divisor.value, MathContext.DECIMAL64))
    }

    fun pow(n: BigDecimal): BigDecimal {
        return BigDecimal(value.pow(n.value.toInt()))
    }

    fun pow(n: Int): BigDecimal {
        return BigDecimal(value.pow(n))
    }

    fun toInt(): Int {
        return value.toInt()
    }

    fun toDouble(): Double {
        return value.toDouble()
    }

    fun toJavaBigDecimal() : JavaBigDecimal {
        return value
    }

    override fun compareTo(other: BigDecimal): Int {
        return value.compareTo(other.value)
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is BigDecimal -> other.value == value
            is JavaBigDecimal -> other == value
            else -> false
        }
    }

    override fun toString(): String {
        return value.toString()
    }

    fun toPlainString(): String {
        return value.toPlainString()
    }

    fun setScale(scale: Int): BigDecimal {
        return BigDecimal(value.setScale(scale, RoundingMode.FLOOR))
    }

    fun format(numberFormat: NumberFormat): String? {
        return numberFormat.format(value)
    }

    companion object {

        fun zero(): BigDecimal = BigDecimal(0)
    }
}

fun JavaBigDecimal.toBigDecimal(): BigDecimal {
    return BigDecimal(this)
}

fun JavaBigDecimal.zero(): BigDecimal {
    return BigDecimal(0)
}
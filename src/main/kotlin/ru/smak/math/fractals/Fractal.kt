package ru.smak.math.fractals

import org.kotlinmath.Complex

interface Fractal {
    fun isInSet(c: Complex): Double
}

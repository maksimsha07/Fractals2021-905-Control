package ru.smak.math.fractals

import org.kotlinmath.Complex

interface Fractal {
    var maxIterations: Int
    fun isInSet(c: Complex): Double
}

package ru.smak.math.fractals

import org.kotlinmath.Complex
import org.kotlinmath.complex
import ru.smak.math.complex.mod2
import kotlin.math.sqrt

class Julia(x0: Double, y0: Double): Fractal {
    private var c0 = complex(x0, y0)
    private var R2 = 4.0
    var R = 2.0
        set(value) {
            field = Math.abs(value).coerceAtLeast(2 * Double.MIN_VALUE)
            R2 = value * value
        }
     var maxIterations: Int = 200

    // метод, который проверяет лежит ли данная точка c во множестве Жюлиа
    override fun isInSet(c: Complex): Double{
        var z = c
        for (i in 0 until maxIterations) {
            z = z * z + c0
            if (z.mod2 >= R2) return i.toDouble() / maxIterations
        }
        return 1.0
    }
}
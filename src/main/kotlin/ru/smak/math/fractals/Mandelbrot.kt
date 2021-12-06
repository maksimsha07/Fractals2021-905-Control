package ru.smak.math.fractals

import org.kotlinmath.Complex
import org.kotlinmath.complex
import ru.smak.math.complex.mod2

object Mandelbrot : Fractal{

    private var R2 = 4.0
    var R = 2.0
        set(value) {
            field = Math.abs(value).coerceAtLeast(2 * Double.MIN_VALUE)
            R2 = value * value
        }
    var maxIterations: Int = 200

    override fun isInSet(c: Complex): Double{
        var z = complex(0, 0)
        for (i in 0 until maxIterations) {
            z = z * z + c
            if (z.mod2 >= R2) return i.toDouble() / maxIterations
        }
        return 1.0
    }
}
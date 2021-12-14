package ru.smak.math

import org.kotlinmath.Complex
import ru.smak.math.complex.mod2
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow



class CatmullRomCurve(
    var p0: Complex, var p1: Complex, var p2: Complex, var p3: Complex, var alpha: Double
) {

    operator fun invoke(t: Double): Complex {
        val k0 = 0.0
        val k1 = getKnotInterval(p0, p1)
        val k2 = getKnotInterval(p1, p2) + k1
        val k3 = getKnotInterval(p2, p3) + k2

        val u = lerpUnclamped(k1, k2, t)
        val a1 = remap(k0, k1, p0, p1, u)
        val a2 = remap(k1, k2, p1, p2, u)
        val a3 = remap(k2, k3, p2, p3, u)
        val b1 = remap(k0, k2, a1, a2, u)
        val b2 = remap(k1, k3, a2, a3, u)
        return remap(k1, k2, b1, b2, u)
    }

    private fun remap(a: Double, b: Double, c: Complex, d: Complex, u: Double): Complex {
        return lerpUnclamped(c, d, (u - a) / (b - a))
    }

    private fun getKnotInterval(a: Complex, b: Complex): Double {
        return (a - b).mod2.pow(0.5 * alpha)
    }
}

class CatmullRom(
    points: List<Complex>,
    alpha: Double
) {
    private val points = points.toList()
    private val curves = ArrayList<CatmullRomCurve>()

    fun getPoints() = points.toList()

    init {
        curves.add(CatmullRomCurve(points[0], points[0], points[1], points[2], alpha))
        var i = 1
        val count = points.size
        while (i != count - 2) {
            curves.add(CatmullRomCurve(points[i-1], points[i], points[i+1], points[i+2], alpha))
            i++
        }
        curves.add(CatmullRomCurve(points[count-3], points[count-2], points[count-1], points[count-1], alpha))
    }

    operator fun invoke(t: Double): Complex {
        val count = curves.size
        var segment = floor((count) * t).toInt()
        segment = max(segment, 0)
        segment = min(segment, count - 1)

        val param = t * (count) - segment

        return curves[segment](param)
    }
}
package ru.smak.math

import org.kotlinmath.Complex
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

fun catmullRom(t: Double, points: List<Complex>): Complex {
    val size = points.size
    var segment = floor((size - 1) * t).toInt()
    segment = max(segment, 0)
    segment = min(segment, size-2)

    val param = t * (size-1) - segment
    return when (segment) {
        0 -> catmullRomSpline(points[0], points[0], points[1], points[2], param)
        size-2 -> catmullRomSpline(points[size - 3], points[size - 2], points[size - 1], points[size - 1], param)
        else -> catmullRomSpline(points[segment - 1], points[segment], points[segment + 1], points[segment + 2], param)
    }
}

fun catmullRomSpline(a: Complex, b: Complex, c: Complex, d: Complex, t: Double): Complex {
    val t1 = (c - a) * 0.5
    val t2 = (d - b) * 0.5

    val h1 = +2 * t * t * t - 3 * t * t + 1 //  2t^3 - 3t^2 + 1
    val h2 = -2 * t * t * t + 3 * t * t     // -2t^3 + 3t^2
    val h3 = t * t * t - 2 * t * t + t      //   t^3 - 2t^2 + t
    val h4 = t * t * t - t * t              //   t^3 -  t^2

    return b * h1 + c * h2 + t1 * h3 + t2 * h4
}
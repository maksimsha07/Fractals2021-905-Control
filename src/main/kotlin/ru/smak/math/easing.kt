package ru.smak.math

import org.kotlinmath.Complex
import kotlin.math.pow

fun lerpUnclamped(p0: Complex, p1: Complex, t: Double): Complex {
    return p0 + (p1 - p0) * t
}

fun lerpUnclamped(p0: Double, p1: Double, t: Double): Double {
    return p0 + (p1 - p0) * t
}

fun easeOutExp(p0: Double, p1: Double, t: Double): Double {
    val param = if (t == 1.0) 1.0 else -(2.0.pow(-50*t))
    return p0 + (p1 - p0) * param
}
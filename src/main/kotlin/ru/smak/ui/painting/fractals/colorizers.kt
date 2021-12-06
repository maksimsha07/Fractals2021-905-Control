package ru.smak.ui.painting.fractals

import java.awt.Color

val colorizers = mutableListOf<(Double)->Color>(
    ::bwFractal,
    ::pinkFractal,
    ::yellowFractal,
)

fun bwFractal(x: Double) =
    Color(
        (1-x.coerceIn(0.0, 1.0)).toFloat(),
        (1-x.coerceIn(0.0, 1.0)).toFloat(),
        (1-x.coerceIn(0.0, 1.0)).toFloat()
    )

fun yellowFractal(x: Double): Color {
    if (x == 1.0) return Color.BLACK
    return Color(
        Math.abs(Math.cos(6*x)).toFloat(),
        Math.abs(Math.cos(12*x)).toFloat(),
        Math.abs(Math.sin(7-7*x)).toFloat()
    )
}

fun pinkFractal(x: Double): Color {
    if (x == 1.0) return Color.BLACK
    return Color(
        Math.abs(Math.cos(Math.log(12.0 * (1.0 - x)))).toFloat(),
        Math.abs(Math.sin(6.0 * (1.0 - x))).toFloat(),
        Math.abs(Math.sin(7.0 - 7.0 * x) * Math.cos(13.0 * x)).toFloat()
    )
}
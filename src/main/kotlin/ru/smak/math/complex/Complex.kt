package ru.smak.math.complex

import org.kotlinmath.Complex

val Complex.mod2: Double
    get() = re * re + im * im
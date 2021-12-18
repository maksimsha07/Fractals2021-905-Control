package ru.smak.ui.painting

import org.kotlinmath.complex
import ru.smak.math.fractals.Fractal
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class FractalPainter(
    val plane: CartesianPlane,
    val frac: Fractal,
) : Painter {

    var colorizer: (Double)->Color = ::getDefaultColor
    private var pool: ExecutorService? = null

    override var size: Dimension
        get() = plane.pixelSize
        set(value) {
            plane.pixelSize = value
        }

    override fun paint(g: Graphics) {
        if (plane.width == 0 || plane.height ==0) return
        val threadCount = 16
        val poolTaskCount = threadCount * 4
        if (pool?.isShutdown == false)
            pool?.shutdown()
        pool = Executors.newFixedThreadPool(threadCount)
        val stripWidth = plane.width / poolTaskCount
        List(poolTaskCount) {
            pool?.submit(Callable{
                val start = it * stripWidth
                val end = (it + 1) * stripWidth - 1 + if (it + 1 == poolTaskCount) plane.width % poolTaskCount else 0
                val img = BufferedImage(end - start + 1, plane.height, BufferedImage.TYPE_INT_RGB)
                val ig = img.graphics
                for (i in start..end) {
                    for (j in 0..plane.height) {
                        val fc = frac.isInSet(
                            complex(
                                plane.xScr2Crt(i),
                                plane.yScr2Crt(j)
                            )
                        )
                        ig.color = colorizer(fc)
                        ig.fillRect(i - start, j, 1, 1)
                    }
                }
                img
            })
        }.forEachIndexed { i, v -> g.drawImage(v?.get(), i * stripWidth, 0, null) }
    }

    fun getDefaultColor(x: Double) =
        if (x == 1.0) Color.BLACK else Color.WHITE
}
package ru.smak.ui

import ru.smak.math.fractals.Mandelbrot
import ru.smak.ui.painting.CartesianPlane
import ru.smak.ui.painting.FractalPainter
import ru.smak.ui.painting.fractals.colorizers
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.GroupLayout
import javax.swing.JFrame
import kotlin.random.Random

class MainFrame : JFrame() {

    private val fractalPanel: SelectablePanel
    private val plane: CartesianPlane
    private var frameColorizer: (Double)->Color
    private var juliaFrame: JFrame

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(600, 400)
        juliaFrame = JFrame()

        plane = CartesianPlane(-2.0, 1.0, -1.0, 1.0)

        fractalPanel = SelectablePanel(
            FractalPainter(plane, Mandelbrot).apply {
                colorizer = colorizers[Random.nextInt(colorizers.size)]
                frameColorizer = colorizer
            }
        ).apply {
            background = Color.WHITE
            addSelectListener { r ->
                with(plane){
                    xSegment = Pair(xScr2Crt(r.x), xScr2Crt(r.x+r.width))
                    ySegment = Pair(yScr2Crt(r.y), yScr2Crt(r.y+r.height))
                }
                repaint()
            }
        }

        // Если пользователь кликнет по панели с изображением множества Мандельброта, появится окно с изображением множества Жюлиа
        fractalPanel.addMouseListener(object : MouseAdapter(){
            override fun mouseClicked(e: MouseEvent?) {
                if (e?.button == 1) {
                    with(plane){
                        juliaFrame.setVisible(false)
                        juliaFrame = (JuliaFrame(xScr2Crt(e.x), yScr2Crt(e.y), frameColorizer, this))
                        juliaFrame.setVisible(true)
                    }
                }
            }
        })

        layout = GroupLayout(contentPane).apply {
            setHorizontalGroup(
                createSequentialGroup()
                    .addGap(4)
                    .addComponent(fractalPanel)
                    .addGap(4)
            )
            setVerticalGroup(
                createSequentialGroup()
                    .addGap(4)
                    .addComponent(fractalPanel)
                    .addGap(4)
            )
        }
    }
}
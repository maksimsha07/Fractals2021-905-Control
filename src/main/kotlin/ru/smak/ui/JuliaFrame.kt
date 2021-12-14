package ru.smak.ui

import ru.smak.math.fractals.Julia
import ru.smak.ui.painting.CartesianPlane
import ru.smak.ui.painting.FractalPainter
import java.awt.Color
import java.awt.Dimension
import javax.swing.GroupLayout
import javax.swing.JFrame

class JuliaFrame(x0: Double, y0: Double, colorizer0: (Double)->Color, xMin: Double, xMax: Double, yMin: Double, yMax: Double) : JFrame() {

    private val fractalPanel: SelectablePanel
    private val plane: CartesianPlane

    init {
        minimumSize = Dimension(600, 400)

        plane = CartesianPlane(xMin, xMax, yMin, yMax)

        fractalPanel = SelectablePanel(
            FractalPainter(plane, Julia(x0, y0)).apply {
                colorizer = colorizer0
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
package ru.smak.ui

import ru.smak.math.fractals.Mandelbrot
import ru.smak.ui.painting.CartesianPlane
import ru.smak.ui.painting.FractalPainter
import ru.smak.ui.painting.fractals.colorizers
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.ArrayDeque
import javax.swing.GroupLayout
import javax.swing.JFrame
import kotlin.random.Random

class MainFrame : JFrame() {

    private val fractalPanel: SelectablePanel
    private val plane: CartesianPlane
    private var frameColorizer: (Double)->Color    // переменная, в которой будет храниться значение колорайзера для фракталов
    private var juliaFrame: JFrame        // окно, в котором будет отображаться множество Жюлиа
    private var buffer = ArrayDeque<List<Pair<Double,Double>>>()


    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(600, 400)
        juliaFrame = JFrame()
        buffer.push(mutableListOf(Pair(-2.0,1.0), Pair(-1.0,1.0)))
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
                    buffer.push(mutableListOf(xSegment,ySegment))
                }
                repaint()
            }
        }
        fractalPanel.isFocusable = true
        // Если пользователь кликнет по панели с изображением множества Мандельброта, появится окно с изображением множества Жюлиа
        // При чем если уже было открыто одно такое окно, новое окно появится вместо него
        fractalPanel.addMouseListener(object : MouseAdapter(){
            override fun mouseClicked(e: MouseEvent?) {
                if (e?.button == 1) {
                    with(plane){
                        juliaFrame.dispose()
                        juliaFrame = JuliaFrame(xScr2Crt(e.x), yScr2Crt(e.y), frameColorizer)
                        juliaFrame.setVisible(true)
                    }

                }
            }
        })

        fractalPanel.addKeyListener(object : KeyAdapter(){
            override fun keyTyped(e: KeyEvent?) {
                super.keyPressed(e)
                if (e != null)
                    if (e.keyChar == 'z') {
                        var segment = buffer.pop()
                        plane.xSegment = segment[0]
                        plane.ySegment = segment[1]
                        if(buffer.size == 0){
                            buffer.push(mutableListOf(Pair(-2.0,1.0), Pair(-1.0,1.0)))
                        }
                    }
                repaint()

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
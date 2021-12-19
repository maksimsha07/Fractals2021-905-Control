package ru.smak.ui

import ru.smak.math.fractals.Mandelbrot
import ru.smak.video.AnimationFrame
import ru.smak.ui.painting.CartesianPlane
import ru.smak.ui.painting.FractalPainter
import ru.smak.ui.painting.fractals.colorizers
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.lang.Math.round
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
                var a= Math.abs(plane.ySegment.first-plane.ySegment.second)
                if (a>=1.0){Mandelbrot.maxIterations = 50
                }else{
                    if (a>=0.5&&a<1.0){ Mandelbrot.maxIterations = 1000 - round(a * 900).toInt()
                    }else {
                        if (a>=0.2&&a<0.5){ Mandelbrot.maxIterations = 1000 - round(a * 2000).toInt()
                        }else {
                            if (a>=0.1&&a<0.2){ Mandelbrot.maxIterations = 1000 - round(a * 4000).toInt()
                            }else{Mandelbrot.maxIterations = 1000 - round(a * 8000).toInt()}
                }}}
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
                    if (e.keyChar == 'z' || e.keyChar == 'я') {
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

        val af = AnimationFrame(plane, Mandelbrot, frameColorizer)
        af.apply {
            isVisible = false
        }

        val menu = Menu(this)
        jMenuBar = menu.jMenuBar

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
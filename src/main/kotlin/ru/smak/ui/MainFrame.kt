package ru.smak.ui

import ru.smak.math.fractals.Detailing
import ru.smak.math.fractals.Mandelbrot
import ru.smak.ui.painting.CartesianPlane
import ru.smak.ui.painting.FractalPainter
import ru.smak.ui.painting.fractals.colorizers
import ru.smak.video.AnimationFrame
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.lang.Math.round
import java.util.*
import javax.swing.GroupLayout
import javax.swing.JFrame
import javax.swing.KeyStroke
import kotlin.random.Random

class MainFrame : JFrame() {

    private val fractalPanel: SelectablePanel
    private val plane: CartesianPlane
    private var frameColorizer: (Double)->Color    // переменная, в которой будет храниться значение колорайзера для фракталов
    private var juliaFrame: JFrame        // окно, в котором будет отображаться множество Жюлиа
    private var buffer = ArrayDeque<List<Double>>()

    init {
        val menu = Menu()
        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(600, 400)
        juliaFrame = JFrame()
        buffer.push(mutableListOf(-2.0,1.0,-1.0,1.0,50.0))
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
                    buffer.push(mutableListOf(xSegment.first,xSegment.second,ySegment.first,ySegment.second,Mandelbrot.maxIterations.toDouble()))
                }
                if(menu.detail.isSelected){
                    Mandelbrot.maxIterations = Detailing(plane).iterations
                }
                else{Mandelbrot.maxIterations = 200}
                repaint()
            }
        }

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
          menu.videoBtn.addKeyListener(object : KeyAdapter(){
            override fun keyTyped(e: KeyEvent?) {
                super.keyPressed(e)
                if (e != null)
                    if (e.keyChar=='я' || e.keyChar=='z') {
                        println("Назад")
                        var segment = buffer.pop()
                        plane.xSegment = Pair(segment[0],segment[1])
                        plane.ySegment = Pair(segment[2],segment[3])
                        Mandelbrot.maxIterations = segment[4].toInt()
                        if(buffer.size == 0){
                            buffer.push(mutableListOf(-2.0,1.0,-1.0,1.0,50.0))
                        }
                        repaint()
                    }

            }
        })

        menu.detail.addKeyListener(object : KeyAdapter(){
            override fun keyTyped(e: KeyEvent?) {
                super.keyPressed(e)
                if (e != null)
                    if (e.keyChar=='я' || e.keyChar=='z') {
                        println("Назад")
                        var segment = buffer.pop()
                        plane.xSegment = Pair(segment[0],segment[1])
                        plane.ySegment = Pair(segment[2],segment[3])
                        Mandelbrot.maxIterations = segment[4].toInt()
                        if(buffer.size == 0){
                            buffer.push(mutableListOf(-2.0,1.0,-1.0,1.0,50.0))
                        }
                        repaint()
                    }

            }
        })

        val af = AnimationFrame(plane, Mandelbrot, frameColorizer)

        menu.videoBtn.addMouseListener(object :MouseAdapter(){
            override fun mouseClicked(e: MouseEvent?) {
                super.mouseClicked(e)
                if(e != null)
                    af.apply {
                        isVisible = true
                    }

            }
        })

        layout = GroupLayout(contentPane).apply {
            setHorizontalGroup(
                createSequentialGroup()
                    .addGap(4)
                    .addGroup(
                        createParallelGroup()
                            .addComponent(menu.menuBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                            .addComponent(fractalPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                    )
                    .addGap(4)
            )
            setVerticalGroup(
                createSequentialGroup()
                    .addGap(4)
                    .addComponent(menu.menuBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(4)
                    .addComponent(fractalPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                    .addGap(4)
            )
        }
    }
}
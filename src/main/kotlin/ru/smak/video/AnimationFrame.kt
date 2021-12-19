package ru.smak.video

import ru.smak.math.fractals.Fractal
import ru.smak.ui.GraphicsPanel
import ru.smak.ui.painting.CartesianPlane
import ru.smak.ui.painting.FractalPainter
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class AnimationFrame(plane: CartesianPlane, frac: Fractal, colorizer_p: (Double) -> Color) : JDialog() {

    private val framePanel = JPanel()
    private val fpsInputLabel = JLabel("FPS:")
    private val fpsInput = JTextField("30")
    private val addFrameButton = JButton("Add frame")
    private val startButton = JButton("Start")
    private var keyFrames = mutableListOf<CartesianPlane>()


    init {
        defaultCloseOperation = HIDE_ON_CLOSE
        minimumSize = Dimension(600, 180)
        fpsInputLabel.alignmentY = JLabel.CENTER_ALIGNMENT
        framePanel.layout = GridLayout(1, 0, 5, 12)

        addFrameButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e?.button == 1) {
                    with(plane) {
                        keyFrames.add(CartesianPlane(xMin, xMax, yMin, yMax))
                    }
                    val p = GraphicsPanel(
                        FractalPainter(keyFrames.last(), frac)
                        .apply {
                            colorizer = colorizer_p
                        }).apply {
                        maximumSize = Dimension(30, 30)
                    }
                    p.addMouseListener(object : MouseAdapter() {
                        override fun mouseClicked(e: MouseEvent?) {
                            keyFrames.removeLast()
                            framePanel.remove(p)
                            framePanel.revalidate()
                            framePanel.repaint()
                            println("Кол-во ключевых кадров: ${keyFrames.size}")
                        }
                    })
                    framePanel.add(p)
                    framePanel.revalidate()
                }
            }
        })

        startButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e?.button == 1) {
                    videoRecordingStarted.add { _, _ -> println("Video recording started") }
                    videoRecordingFinished.add { _, _ -> println("Video recording finished") }
                    imageCreated.add { _, data -> println("Created image: ${data.timestamp} / ${data.last}") }
                    imageCreatingFinished.add { _, _ -> println("Image creating finished") }
                    imageRecorded.add { _, data -> println("Recorded image: ${data.timestamp} / ${data.last}") }
                    VideoRecorder.frameWidth = 640
                    VideoRecorder.frameHeight = 480
                    VideoRecorder.duration = 30
                    VideoRecorder.fps = fpsInput.text.toInt()
                    VideoRecorder.createVideoAsync(keyFrames, "fractal.avi", "avi")
                }
            }
        })


        layout = GroupLayout(contentPane).apply {
            autoCreateGaps = true
            autoCreateContainerGaps = true
            setHorizontalGroup(
                createParallelGroup()
                    .addComponent(
                        framePanel,
//                        GroupLayout.PREFERRED_SIZE,
//                        GroupLayout.PREFERRED_SIZE,
//                        GroupLayout.PREFERRED_SIZE
                    )
                    //.addGap(20)
                    .addGroup(
                        createSequentialGroup()
                            .addComponent(fpsInputLabel)
                            .addComponent(fpsInput, 50, GroupLayout.PREFERRED_SIZE, 50)
                            .addComponent(addFrameButton)
                            .addPreferredGap(
                                LayoutStyle.ComponentPlacement.RELATED,
                                GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE
                            )
                            .addComponent(startButton)

                    )
            )
            setVerticalGroup(
                createSequentialGroup()
                    //.addGap(20)
                    .addComponent(
                        framePanel,
                        100,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE
                    )
                    //.addGap(20)
                    .addGroup(
                        createParallelGroup()
                            .addComponent(fpsInputLabel, 25, 25, 25)
                            //.addGap(10)
                            .addComponent(fpsInput, 26, 26, 26)
                            //.addGap(50)
                            .addComponent(addFrameButton)
                            //.addGap(10)
                            .addComponent(startButton)
                    )
                //.addGap(20)
            )
        }
    }
}
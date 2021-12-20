package ru.smak.video

import io.humble.video.Video
import ru.smak.math.fractals.Fractal
import ru.smak.ui.GraphicsPanel
import ru.smak.ui.painting.CartesianPlane
import ru.smak.ui.painting.FractalPainter
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.*

class AnimationFrame(plane: CartesianPlane, frac: Fractal, colorizer_p: (Double) -> Color) : JDialog() {

    private val framePanel = JPanel()
    private val fpsInputLabel = JLabel("FPS:")
    private val fpsInput = JTextField("30")
    private val durationInputLabel = JLabel("Duration: ")
    private val durationInput = JTextField("10")
    private val widthInputLabel = JLabel("Width: ")
    private val widthInput = JTextField("640")
    private val heightInputLabel = JLabel("Height: ")
    private val heightInput = JTextField("380")
    private val addFrameButton = JButton("Add frame")
    private val startButton = JButton("Start")
    private val progressBar = JProgressBar()
    private var keyFrames = mutableListOf<CartesianPlane>()


    init {
        defaultCloseOperation = HIDE_ON_CLOSE
        minimumSize = Dimension(600, 240)
        fpsInputLabel.alignmentY = JLabel.CENTER_ALIGNMENT
        durationInputLabel.alignmentY = JLabel.CENTER_ALIGNMENT
        widthInputLabel.alignmentY = JLabel.CENTER_ALIGNMENT
        heightInputLabel.alignmentY = JLabel.CENTER_ALIGNMENT
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
                            println("Кол-во ключевых кадров is ${keyFrames.size}")
                        }
                    })
                    framePanel.add(p)
                    framePanel.revalidate()
                }
            }
        })

        startButton.addMouseListener(object : MouseAdapter() {
            val x = AtomicInteger(0)
            override fun mouseClicked(e: MouseEvent?) {
                if (e?.button == 1) {
                    videoRecordingStarted.add { _, _ -> println("Video recording started") }
                    videoRecordingFinished.add { _, _ -> println("Video recording finished") }
                    imageCreated.add { _, data ->
                        println("Created image: ${data.timestamp} / ${data.last}")
                        progressBar.value = (x.incrementAndGet().toFloat() / (data.last+1) * 100).toInt()
                    }
                    imageCreatingFinished.add { _, _ -> println("Image creating finished") }
                    imageRecorded.add { _, data -> println("Recorded image: ${data.timestamp} / ${data.last}") }
                    VideoRecorder.frameWidth = widthInput.text.toInt()
                    VideoRecorder.frameHeight = heightInput.text.toInt()
                    VideoRecorder.duration = durationInput.text.toInt()
                    VideoRecorder.fps = fpsInput.text.toInt()
                    VideoRecorder.createVideoAsync(keyFrames, "fractal.avi", "avi")
                }
            }
        })


        layout = GroupLayout(contentPane).apply {
            autoCreateGaps = true
            autoCreateContainerGaps = true
            linkSize(fpsInputLabel, durationInputLabel)
            linkSize(widthInputLabel, heightInputLabel)
            linkSize(addFrameButton, startButton)
//            linkSize(progressBar, framePanel)
            setHorizontalGroup(
                createParallelGroup()
                    .addComponent(
                        framePanel,
//                        GroupLayout.PREFERRED_SIZE,
                    )
                    .addComponent(progressBar)
                    .addGroup(
                        createSequentialGroup()
                            .addComponent(fpsInputLabel)
                            .addComponent(fpsInput, 50, GroupLayout.PREFERRED_SIZE, 50)
                            .addPreferredGap(
                                LayoutStyle.ComponentPlacement.RELATED,
                                GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE
                            )
                            .addComponent(heightInputLabel)
                            .addComponent(heightInput, 50, GroupLayout.PREFERRED_SIZE, 50)
                            .addPreferredGap(
                                LayoutStyle.ComponentPlacement.RELATED,
                                GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE
                            )
                            .addComponent(addFrameButton)
                    )
                    .addGroup(
                        createSequentialGroup()
                            .addComponent(durationInputLabel)
                            .addComponent(durationInput, 50, GroupLayout.PREFERRED_SIZE, 50)
                            .addPreferredGap(
                                LayoutStyle.ComponentPlacement.RELATED,
                                GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE
                            )
                            .addComponent(widthInputLabel)
                            .addComponent(widthInput, 50, GroupLayout.PREFERRED_SIZE, 50)
                            .addPreferredGap(
                                LayoutStyle.ComponentPlacement.RELATED,
                                GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE
                            )
                            .addComponent(startButton)
                    )
            )
            setVerticalGroup(
                createSequentialGroup()
                    .addComponent(
                        framePanel,
                        100,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE
                    )
                    .addComponent(progressBar)
                    .addGroup(
                        createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(
                                createSequentialGroup()
                                    .addComponent(fpsInputLabel, 25, 25, 25)
                                    .addComponent(durationInputLabel, 25, 25, 25)
                            )
                            .addGroup(
                                createSequentialGroup()
                                    .addComponent(fpsInput, 26, 26, 26)
                                    .addComponent(durationInput, 26, 26, 26)

                            )
                            .addGroup(
                                createSequentialGroup()
                                    .addComponent(widthInputLabel, 25, 25, 25)
                                    .addComponent(heightInputLabel, 25, 25, 25)

                            )
                            .addGroup(
                                createSequentialGroup()
                                    .addComponent(widthInput, 26, 26, 26)
                                    .addComponent(heightInput, 26, 26, 26)

                            )
                            .addGroup(
                                createSequentialGroup()
                                    .addComponent(addFrameButton)
                                    .addComponent(startButton)
                            )
                    )
                    .addPreferredGap(
                        LayoutStyle.ComponentPlacement.RELATED,
                        GroupLayout.PREFERRED_SIZE, Int.MAX_VALUE
                    )
            )
        }
    }
}
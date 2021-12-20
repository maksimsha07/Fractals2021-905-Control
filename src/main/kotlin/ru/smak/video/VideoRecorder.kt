package ru.smak.video

import io.humble.video.*
import io.humble.video.awt.MediaPictureConverter
import io.humble.video.awt.MediaPictureConverterFactory
import org.kotlinmath.Complex
import org.kotlinmath.complex
import ru.smak.events.Event
import ru.smak.events.colorSchemeChanged
import ru.smak.math.catmullRom
import ru.smak.math.easeOutExp
import ru.smak.math.fractals.Mandelbrot
import ru.smak.math.lerpUnclamped
import ru.smak.ui.painting.CartesianPlane
import ru.smak.ui.painting.fractals.pinkFractal
import ru.smak.ui.painting.fractals.yellowFractal
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.stream.Collectors
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max


internal val videoRecordingStarted = Event<Unit>()
internal val videoRecordingInterrupted = Event<Unit>()
internal val imageCreated = Event<ImageData>()
internal val imageCreatingFinished = Event<Unit>()
internal val imageRecorded = Event<ImageData>()
internal val videoRecordingFinished = Event<Unit>()

internal data class ImageData(val timestamp: Int, val last: Int)

internal object VideoRecorder {

    var frameWidth: Int = 1280
    var frameHeight: Int = 720
    var duration: Int = 10
    var fps: Int = 60

    val aspectRatio: Double
    get() = frameWidth.toDouble() / frameHeight

    init {
        colorSchemeChanged.add { _, function ->
            colorizer = function
        }
    }

    fun createVideoAsync(keyFrames: List<CartesianPlane>, fileName: String, formatType: String) = CompletableFuture
        .supplyAsync {
            videoRecordingStarted(Unit)
            val images = createImages(keyFrames)
            imageCreatingFinished(Unit)
            images
        }
        .thenAccept { createVideo(it, fileName, formatType) }
        .thenAccept { videoRecordingFinished(Unit) }


    private fun createVideo(images: List<BufferedImage>, fileName: String, formatType: String) {
        val frameDuration = Rational.make(1, fps)
        val muxer = Muxer.make(fileName, null, formatType)
        val format = muxer.format
        val codec = Codec.findEncodingCodec(format.defaultVideoCodecId)

        val encoder = Encoder.make(codec)

        encoder.apply {
            width = frameWidth
            height = frameHeight
            pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P
            timeBase = frameDuration
            if (format.getFlag(ContainerFormat.Flag.GLOBAL_HEADER)) {
                setFlag(Coder.Flag.FLAG_GLOBAL_HEADER, true)
            }
            open(null, null)
        }
        muxer.addNewStream(encoder)
        muxer.open(null, null)

        val picture = MediaPicture.make(
            encoder.width, encoder.height, encoder.pixelFormat
        )
        picture.timeBase = frameDuration
        val packet = MediaPacket.make()

        val frameCount = fps * duration

        var converter: MediaPictureConverter? = null

        repeat(frameCount) { i ->
            val img = images[i]
            val newImg = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB)
            newImg.graphics.drawImage(img, 0, 0, null)
            val image = convertToType(newImg, BufferedImage.TYPE_3BYTE_BGR)
            if (converter == null)
                converter = MediaPictureConverterFactory.createConverter(image, picture)
            converter?.toPicture(picture, image, i.toLong())
            do {
                encoder.encode(packet, picture)
                if (packet.isComplete) muxer.write(packet, false)
            } while (packet.isComplete)
            imageRecorded(ImageData(i, frameCount-1))
        }
    }

    private fun createImages(keyFrames: List<CartesianPlane>): List<BufferedImage> {
        val centers = getCenters(keyFrames)
        val first = keyFrames[0]
        val last = keyFrames[keyFrames.size - 1]
        val startHeight = first.run { max(xMax - xMin, yMax - yMin) }
        val endHeight = last.run { max(xMax - xMin, yMax - yMin) }
        val endLinearZoom = ln(endHeight) - ln(startHeight)
        val lastFrame = fps * duration - 1
        return IntRange(0, lastFrame).toList().parallelStream()
            .map { getInbetween(it, lastFrame, centers, startHeight, endLinearZoom) }
            .map { (plane, data) ->
                val img = drawImage(plane)
                imageCreated(data)
                img
            }
            .collect(Collectors.toList())
    }

    private fun getCenters(keyFrames: List<CartesianPlane>): List<Complex> {
        return keyFrames.map { complex((it.xMin + it.xMax) * 0.5, (it.yMin + it.yMax) * 0.5) }
    }

    private fun getInbetween(
        frame: Int, lastFrame: Int, centers: List<Complex>, startHeight: Double, endLinearZoom: Double
    ): Pair<CartesianPlane, ImageData> {

        val t = frame.toDouble() / lastFrame
        val center = catmullRom(easeOutExp(0.0, 1.0, t), centers)
        val zoom = exp(lerpUnclamped(0.0, endLinearZoom, t))
        val hh = startHeight * zoom * 0.5
        val hw = hh * aspectRatio
        return Pair(
            CartesianPlane(center.re - hw, center.re + hw, center.im - hh, center.im + hh)
                .apply { width = frameWidth; height = frameHeight },
            ImageData(frame, lastFrame)
        )
    }

    private fun drawImage(plane: CartesianPlane): BufferedImage {
        val img = BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB)
        paint(img.graphics, plane)
        return img
    }

    private fun convertToType(sourceImage: BufferedImage, targetType: Int): BufferedImage {
        val image: BufferedImage
        if (sourceImage.type == targetType) image = sourceImage else {
            image = BufferedImage(
                sourceImage.width,
                sourceImage.height, targetType
            )
            image.graphics.drawImage(sourceImage, 0, 0, null)
        }
        return image
    }

    var colorizer: (Double)-> Color = ::yellowFractal

    private val threadCount = 16
    private val poolTaskCount = threadCount * 4
    private var pool = Executors.newFixedThreadPool(threadCount)
    private var frac = Mandelbrot


    private fun paint(g: Graphics, plane: CartesianPlane) {
        if (plane.width == 0 || plane.height ==0) return
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
}
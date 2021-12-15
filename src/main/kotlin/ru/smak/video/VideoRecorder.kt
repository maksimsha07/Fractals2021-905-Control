package ru.smak.video

import io.humble.video.*
import io.humble.video.awt.MediaPictureConverterFactory
import org.kotlinmath.Complex
import org.kotlinmath.complex
import ru.smak.math.catmullRom
import ru.smak.math.fractals.Mandelbrot
import ru.smak.ui.painting.CartesianPlane
import ru.smak.ui.painting.FractalPainter
import java.awt.image.BufferedImage
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors


internal object VideoRecorder {

    var frameWidth: Int = 1280
    var frameHeight: Int = 720
    var duration: Int = 10
    var fps: Int = 60

    val aspectRatio: Double
    get() = frameWidth.toDouble() / frameHeight

    private lateinit var createVideoTask: CompletableFuture<Void>

    fun createVideoAsync(keyFrames: List<CartesianPlane>, fileName: String, formatType: String) {
        if (::createVideoTask.isInitialized) {
            createVideoTask.cancel(false)
        }
        createVideoTask = CompletableFuture.supplyAsync { createImages(keyFrames) }
            .thenAccept { createVideo(it, fileName, formatType) }
    }

    private fun createVideo(images: List<BufferedImage>, fileName: String, formatType: String) {
        val frameDuration = Rational.make(1, fps)
        val muxer = Muxer.make(fileName, null, formatType)
        val format = muxer.format
        val codec = Codec.findEncodingCodec(Codec.ID.CODEC_ID_H264)

        val encoder = Encoder.make(codec)

        encoder.apply {
            width = frameWidth
            height = frameHeight
            pixelFormat = PixelFormat.Type.PIX_FMT_RGB24
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

        repeat(fps * duration) { i ->
            val img = images[i]
            val image = convertToType(img, BufferedImage.TYPE_3BYTE_BGR)
            val converter = MediaPictureConverterFactory.createConverter(image, picture)
            converter.toPicture(picture, image, i.toLong())
            do {
                encoder.encode(packet, picture)
                if (packet.isComplete) muxer.write(packet, false)
            } while (packet.isComplete)
        }
    }

    private fun createImages(keyFrames: List<CartesianPlane>): List<BufferedImage> {
        val (minCorners, maxCorners) = getCorners(keyFrames)
        val lastFrame = fps * duration - 1
        return IntRange(0, lastFrame).toList().parallelStream()
            .map { getInbetween(it, lastFrame, minCorners, maxCorners) }
            .map { drawImage(FractalPainter(it, Mandelbrot)) }
            .collect(Collectors.toList())
    }

    private fun getCorners(keyFrames: List<CartesianPlane>): Pair<List<Complex>, List<Complex>> {
        val minCorners = ArrayList<Complex>()
        val maxCorners = ArrayList<Complex>()
        keyFrames.forEach {
            // HACK: Здесь проводится принудительная коррекция соотношения сторон в ключевых кадрах
            val (halfWidth, halfHeight) = if (it.width > it.height) {
                val hw = it.width * 0.5
                val hh = hw / aspectRatio
                Pair(hw, hh)
            } else {
                val hh = it.height * 0.5
                val hw = hh * aspectRatio
                Pair(hw, hh)
            }
            val (x, y) = Pair((it.xMin + it.xMax) * 0.5, (it.yMin + it.yMax) * 0.5)
            minCorners.add(complex(x - halfWidth, y - halfHeight))
            maxCorners.add(complex(x + halfWidth, y + halfHeight))
        }
        return Pair(minCorners, maxCorners)
    }

    private fun getInbetween(
        frame: Int, lastFrame: Int, minCorners: List<Complex>, maxCorners: List<Complex>
    ): CartesianPlane {

        val t = frame.toDouble() / (lastFrame)
        val p0 = catmullRom(t, minCorners)
        val p1 = catmullRom(t, maxCorners)
        return CartesianPlane(p0.re, p1.re, p0.im, p1.im)
    }

    private fun drawImage(fractalPainter: FractalPainter): BufferedImage {
        val img = BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB)
        fractalPainter.paint(img.createGraphics())
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
}
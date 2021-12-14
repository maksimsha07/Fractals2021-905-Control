package ru.smak.video

import io.humble.video.*
import io.humble.video.awt.MediaPictureConverterFactory
import org.kotlinmath.Complex
import org.kotlinmath.complex
import ru.smak.math.catmullRom
import ru.smak.ui.painting.CartesianPlane
import java.awt.image.BufferedImage
import kotlin.math.max


internal object VideoRenderer {

    var frameWidth: Int = 1280
    var frameHeight: Int = 720
    var duration: Int = 10
    var fps: Int = 60

    val aspectRatio: Double
    get() = frameWidth.toDouble() / frameHeight


    fun createVideo(keyFrames: List<CartesianPlane>, fileName: String, formatType: String) {
        val images = createFrameData(keyFrames)
        val frameDuration = Rational.make(1, fps)
        val muxer = Muxer.make(fileName, null, formatType)
        val format = muxer.format
        val codec = Codec.findEncodingCodec(format.defaultVideoCodecId)

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
            val img = BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB)//images[i]
            val newImg = BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB)
            newImg.graphics.drawImage(img, 0, 0, null)
            val image = convertToType(newImg, BufferedImage.TYPE_3BYTE_BGR)
            val converter = MediaPictureConverterFactory.createConverter(image, picture)
            converter.toPicture(picture, image, i.toLong())
            do {
                encoder.encode(packet, picture)
                if (packet.isComplete) muxer.write(packet, false)
            } while (packet.isComplete)
        }
    }

    private fun createFrameData(keyFrames: List<CartesianPlane>): List<CartesianPlane> {
        val minCorners = ArrayList<Complex>()
        val maxCorners = ArrayList<Complex>()
        keyFrames.forEach {
            // HACK: Здесь проводится принудительная коррекция соотношения сторон в ключевых кадрах
            val halfWidth = max(it.width, it.height) * 0.5f
            val halfHeight = halfWidth / aspectRatio * 0.5f
            val (x, y) = Pair((it.xMin + it.xMax) * 0.5, (it.yMin + it.yMax) * 0.5)
            minCorners.add(complex(x - halfWidth, y - halfHeight))
            maxCorners.add(complex(x + halfWidth, y + halfHeight))
        }
        val out = mutableListOf<CartesianPlane>()
        val frameCount = fps * duration
        repeat(frameCount) {
            val t = it.toDouble() / (frameCount-1)
            val p0 = catmullRom(t, minCorners)
            val p1 = catmullRom(t, maxCorners)
            out.add(CartesianPlane(p0.re, p1.re, p0.im, p1.im))
        }
        return out
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
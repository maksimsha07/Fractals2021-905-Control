package ru.smak.video

import io.humble.video.*
import io.humble.video.awt.MediaPictureConverterFactory
import org.kotlinmath.Complex
import org.kotlinmath.ZERO
import org.kotlinmath.complex
import ru.smak.events.Event
import ru.smak.math.CatmullRom
import ru.smak.math.complex.mod2
import ru.smak.ui.painting.CartesianPlane
import java.awt.image.BufferedImage
import java.lang.Double.min
import kotlin.math.ln

/**
 * (Consumer) Класс, осуществляющий создание видео
 * @param frameWidth ширина кадра
 * @param frameHeight высота кадра
 * @param duration длительность видео
 * @param fps количество кадров в секунду
 */
internal class VideoRenderer(
    var frameWidth: Int = 1280, var frameHeight: Int = 720,
    var duration: Int = 10, var fps: Int = 60
) {

    init {
        onCreateVideoPressed.removeAll()
        onCreateVideoPressed.add(this::createVideo)
    }

    private fun createVideo(event: Event<CreateVideoData>, data: CreateVideoData) {

        val frameDuration = Rational.make(1, fps)
        val muxer = Muxer.make(data.fileName, null, data.formatType)
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
            val img = BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB)//queue.take()
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
        val points = keyFrames.map { complex((it.xMin + it.xMax) * 0.5, (it.yMin + it.yMax) * 0.5) }
        val catmullRom = CatmullRom(points, 0.5)
        val v0 = keyFrames.first()
        val v1 = keyFrames.last()
        val finalZoom = min(v1.width.toDouble() / v0.width, v1.height.toDouble() / v0.height)
        val finalLinZoom = ln(finalZoom)
        val zoomStep = finalLinZoom / (fps * duration)

        val out = mutableListOf<CartesianPlane>()
        repeat(fps * duration) {

        }
        return out
    }

    private fun ease(t: Double, from: Complex, to: Complex): Complex {
        return ZERO
    }

    /**
     * Convert a [BufferedImage] of any type, to [BufferedImage] of a
     * specified type. If the source image is the same type as the target type,
     * then original image is returned, otherwise new image of the correct type is
     * created and the content of the source image is copied into the new image.
     *
     * @param sourceImage the image to be converted
     * @param targetType  the desired BufferedImage type
     * @return a BufferedImage of the specifed target type.
     * @see BufferedImage
     */
    private fun convertToType(sourceImage: BufferedImage, targetType: Int): BufferedImage {
        val image: BufferedImage

        // if the source image is already the target type, return the source image
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
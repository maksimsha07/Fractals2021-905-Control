package ru.smak

import ru.smak.ui.MainFrame
import ru.smak.ui.painting.CartesianPlane
import ru.smak.video.*
import ru.smak.video.VideoRecorder
import ru.smak.video.imageCreated
import ru.smak.video.imageCreatingFinished
import ru.smak.video.videoRecordingFinished
import ru.smak.video.videoRecordingStarted
import java.util.concurrent.CompletableFuture

fun main(args: Array<String>) {
    MainFrame().apply {
        isVisible = true
    }
}

fun testVideo() {
    val keyFrames = listOf(
        CartesianPlane(-2.0, 1.0, -1.0, 1.0),
        CartesianPlane(-1.807625649913345, -1.490467937608319, -0.07909604519774005, 0.096045197740113),
        CartesianPlane(-1.6542686278455767, -1.5993019532693158, -0.015273388872929186, 0.008474576271186474),
        CartesianPlane(-1.6337871113223599, -1.618926103984584, -0.007558654433456585, 0.006126613615694812),
        CartesianPlane(-1.6330659532539755, -1.6313918363095121, -9.093151553378268E-4, 3.6643017127798144E-4),
        CartesianPlane(-1.632050457707074, -1.631710992052824, -1.0927147593469278E-4, 6.371094123355239E-5),
        CartesianPlane(-1.6319645617183036, -1.6318721941139411, -2.3757569142481187E-5, 3.1948632996445224E-5),
        CartesianPlane(-1.6319461522304324, -1.6319214995249354, -7.077180931390225E-6, 5.826515609265043E-6),
        CartesianPlane(-1.6319338472405136, -1.6319298737541856, -9.533927426046741E-7, 1.3430278281899076E-6),
        CartesianPlane(-1.6319318708270367, -1.6319310100197733, -2.0089334652509357E-7, 2.8563643542291084E-7),
        CartesianPlane(-1.6319314769741466, -1.6319312576696965, -8.819435748629032E-8, 9.872103896831026E-8),
        CartesianPlane(-1.6319314572101407, -1.6319314135012815, -2.1665148578720633E-8, 1.2127465469568748E-8),
        CartesianPlane(-1.6319314332725332, -1.6319314233490314, -2.095979426462657E-9, 3.822501000073901E-9),
        CartesianPlane(-1.63193142833658, -1.6319314262555684, -6.915942405048295E-10, 5.957588466231787E-10),
        CartesianPlane(-1.6319314277306702, -1.6319314269263971, -2.370204103042617E-10, 2.575559169539562E-10),
        CartesianPlane(-1.6319314275815242, -1.6319314270741492, -1.7554764646425721E-10, 1.3321328100485622E-10),
        CartesianPlane(-1.6319314274971082, -1.6319314274478656, -1.3317328641502677E-11, 2.1570911750487527E-11),
        CartesianPlane(-1.6319314274808077, -1.6319314274603256, -6.2214153414368745E-12, 8.955954772592763E-12),
        CartesianPlane(-1.6319314274763705, -1.6319314274726788, -1.505283385099982E-12, 1.3243957887021534E-12),
        CartesianPlane(-1.6319314274758139, -1.6319314274737728, -8.178472016339263E-13, 7.408743771553854E-13),
        CartesianPlane(-1.631931427475202, -1.6319314274743282, -1.7938779789254156E-13, 3.710082398155488E-13),
        CartesianPlane(-1.6319314274748764, -1.6319314274747143, 1.3001562443488775E-13, 2.4817974552476024E-13),
        CartesianPlane(-1.6319314274748207, -1.6319314274747991, 2.0612132954361919E-13, 2.33492679626584E-13),
    )
    videoRecordingStarted.add { _, _ -> println("Video recording started") }
    videoRecordingFinished.add { _, _ -> println("Video recording finished") }
    imageCreated.add { _, data -> println("Created image: ${data.timestamp} / ${data.last}") }
    imageCreatingFinished.add { _, _ -> println("Image creating finished") }
    imageRecorded.add { _, data -> println("Recorded image: ${data.timestamp} / ${data.last}") }
    VideoRecorder.frameWidth = 640
    VideoRecorder.frameHeight = 480
    VideoRecorder.duration = 30
    VideoRecorder.fps = 24
    VideoRecorder.createVideoAsync(keyFrames, "fractal5.avi", "avi")
}


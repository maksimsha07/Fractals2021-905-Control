package ru.smak.video

import ru.smak.events.Event
import ru.smak.ui.painting.CartesianPlane

internal class CreateVideoData(
    val fileName: String,
    val formatType: String,
    val keyFrames: List<CartesianPlane>
)

internal val onCreateVideoPressed = Event<CreateVideoData>()
package ru.smak.events

// TODO: Это событие должно триггериться, если изменилась цветовая схема
val colorSchemeChanged = Event<(Double) -> Float>()
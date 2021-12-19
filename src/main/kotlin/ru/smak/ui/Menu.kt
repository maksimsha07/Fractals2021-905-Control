package ru.smak.ui

import javax.swing.*

class Menu (val mf: MainFrame): JFrame() {
    var menuBar = JMenuBar()

    init {
        //заполняем меню
        fillMenuBar()
        jMenuBar = menuBar
    }

    private fun fillMenuBar() {
//отдельные функции для создания вложенных пунктов меню
        menuBar.add(createFileMenu())
        menuBar.add(createEditMenu())
    }

    private fun createEditMenu(): JMenu{

    }

    private fun createFileMenu(): JMenu{

    }
}
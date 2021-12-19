package ru.smak.ui

import javax.swing.*

class Menu (val mf: MainFrame): JFrame() {
    var menuBar = JMenuBar()
    val f1 : JRadioButtonMenuItem = JRadioButtonMenuItem("Множество Мандельброта")
    val f2 = JRadioButtonMenuItem("Множество Жюли")

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
        //Создание выпадающего меню
        val fileMenu = JMenu("Файл")
        //Пункт меню "Открыть"
        val open = JMenuItem("Открыть")
        open.addActionListener {

        }
        //Пункт меню "Сохранить как..."
        val save = JMenu("Сохранить как...")
        //Подпункты "Сохранить как..."
        val format1 = JMenuItem("Собственный формат")
        format1.addActionListener {
            f1.doClick()
        }
        val format2 = JMenuItem("Изображение")
        format2.addActionListener {
        }
        //Добавление подпунктов в пункты
        save.add(format1)
        save.add(format2)
        fileMenu.add(open)
        // Добавление разделителя
        fileMenu.addSeparator()
        fileMenu.add(save)
        return fileMenu
    }
}
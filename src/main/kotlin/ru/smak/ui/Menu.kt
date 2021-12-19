package ru.smak.ui

import javax.swing.*

class Menu (val mf: MainFrame): JFrame() {
    var menuBar = JMenuBar()
    val bgClr = ButtonGroup()
    val detail = JCheckBox("Детализация ")
    val f1 : JRadioButtonMenuItem = JRadioButtonMenuItem("Множество Мандельброта")
    val f2 = JRadioButtonMenuItem("Множество Жюли")
    val bgF = ButtonGroup()

    init {
        //заполняем меню
        fillMenuBar()
        jMenuBar = menuBar
    }

    private fun fillMenuBar() {
//отдельные функции для создания вложенных пунктов меню
        menuBar.add(createFileMenu())
        menuBar.add(createEditMenu())
        //кнопка для записи видео
        val videoBtn = JButton("Записать видео ")
        menuBar.add(videoBtn)
        videoBtn.addActionListener {
        }
        //поле с галочкой для детализации
        detail.addActionListener {
        }
        menuBar.add(detail)
    }

    private fun createEditMenu(): JMenu{
        val editMenu = JMenu("Настройки")
        val clrSchemes = JMenu("Цветовая схема")
        val fractal = JMenu("Фрактал")

        // меню-переключатели для цветов
        val clr1 = JRadioButtonMenuItem("Цветовая схема 1")
        clr1.actionCommand = "colorScheme1"
        val clr2 = JRadioButtonMenuItem("Цветовая схема 2")
        clr2.actionCommand = "colorScheme2"
        val clr3 = JRadioButtonMenuItem("Цветовая схема 3")
        clr3.actionCommand = "colorScheme3"
        val clr4 = JRadioButtonMenuItem("Цветовая схема 4")
        clr4.actionCommand = "colorScheme4"
        val clr5 = JRadioButtonMenuItem("Цветовая схема 5")
        clr5.actionCommand = "colorScheme5"

        clr1.doClick()

        // организуем переключатели в логическую группу
        bgClr.add(clr1)
        bgClr.add(clr2)
        bgClr.add(clr3)
        bgClr.add(clr4)
        bgClr.add(clr5)
        clrSchemes.add(clr1)
        clrSchemes.add(clr2)
        clrSchemes.add(clr3)
        clrSchemes.add(clr4)
        clrSchemes.add(clr5)
        bgClr.selection.actionCommand

        //меню-переключатели для выбора фрактала
        f1.addActionListener {
        }
        f1.actionCommand="1"
        f1.doClick()

        f2.actionCommand="2"
        f2.addActionListener {
        }

        // организуем переключатели в логическую группу
        bgF.add(f1)
        bgF.add(f2)
        fractal.add(f1)
        fractal.add(f2)
        bgF.selection.actionCommand

        // добавим все в меню
        editMenu.add(clrSchemes)
        editMenu.add(JSeparator()) //разделитель
        editMenu.add(fractal)
        return editMenu
    }
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
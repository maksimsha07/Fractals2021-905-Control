package ru.smak.ui

import ru.smak.ui.painting.CartesianPlane
import java.awt.image.BufferedImage
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.charset.Charset
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

object SaveFractal {
    operator fun invoke(plane:CartesianPlane) {
        fun save(name: String,strf : String){
            // открытие файла
            val file = FileOutputStream(name)//задается файл, в который производится запись
            val st = ObjectOutputStream(file)//записываем строку
            //запись в файл
            st.writeObject(strf)
            //закрытие файла
            st.close()
        }
        val fileChooser = JFileChooser()
        with(fileChooser) {
            dialogTitle = "Сохранение файла..."
            val filter1 = FileNameExtensionFilter("txt", "txt")
            addChoosableFileFilter(filter1)
        }
        fileChooser.fileSelectionMode = JFileChooser.OPEN_DIALOG
        val result = fileChooser.showSaveDialog(fileChooser)
        if (result == JFileChooser.APPROVE_OPTION) {
            var str = fileChooser.selectedFile.absolutePath
            if (fileChooser.selectedFile.extension == "") {
                if (fileChooser.fileFilter.description != "All Files")
                    str = str + "." + fileChooser.fileFilter.description
                else str += ".txt"
            }
            val xMin = BigDecimal(plane.xMin).setScale(2, RoundingMode.HALF_EVEN).toString()
            val xMax = BigDecimal(plane.xMax).setScale(2, RoundingMode.HALF_EVEN).toString()
            val yMin = BigDecimal(plane.yMin).setScale(2, RoundingMode.HALF_EVEN).toString()
            val yMax = BigDecimal(plane.yMax).setScale(2, RoundingMode.HALF_EVEN).toString()
            val strf="$xMin,$xMax,$yMin,$yMax"
            save(str,strf)
            JOptionPane.showMessageDialog(fileChooser,
                "Файл '" + str +
                        "' сохранен")

        }
    }
    /**
     chButtonM.addActionListener {
    run {
    SaveFractal.invoke(plane)
    }
    }
     */
}
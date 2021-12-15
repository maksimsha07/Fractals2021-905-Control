package ru.smak.ui

import ru.smak.ui.painting.Painter
import java.awt.Color
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.*
import java.lang.Integer.min

class SelectablePanel(vararg painters: Painter) : GraphicsPanel(*painters){

    private var pt1: Point? = null
    private var pt2: Point? = null

    private var rect: Rectangle? = null
        get(){
            pt1?.let { p1 ->
                pt2?.let { p2 ->
                    return Rectangle(
                        min(p1.x, p2.x),
                        min(p1.y, p2.y),
                        Math.abs(p2.x - p1.x),
                        Math.abs(p2.y - p1.y)
                    )
                }
            }
            return null
        }

    private var selectListeners: MutableList<(Rectangle)->Unit> = mutableListOf()
    fun addSelectListener(l: (Rectangle)->Unit){
        selectListeners.add(l)
    }
    fun removeSelectListener(l: (Rectangle)->Unit){
        selectListeners.remove(l)
    }

    init {
        addMouseListener(object : MouseAdapter(){
            override fun mousePressed(e: MouseEvent?) {
                super.mousePressed(e)
                with(graphics){
                    setXORMode(Color.WHITE)
                    fillRect(2*width, 0, 1, 1)
                    setPaintMode()
                }
                pt1 = e?.point
            }

            override fun mouseReleased(e: MouseEvent?) {
                super.mouseReleased(e)
                var panelAspectRatio = width/height.toDouble()  //Разрешение панели
                val minRes:Double = panelAspectRatio-0.2    //Разрешение панели - погрешность
                val maxRes:Double = panelAspectRatio+0.2    //Разрешение панели + погрешность

                rect?.let {
                    var rectAspectRatio = it.width/it.height.toDouble()

                    if((rectAspectRatio<minRes)||(rectAspectRatio>maxRes)) { //Если выбранная область не подходит нашему разрешению

                    }
                }


                drawSelectRect()
                rect?.let{ r->
                    selectListeners.forEach { it(r)}
                }
                pt1 = null
                pt2 = null
                rect = null
            }
        })

        addMouseMotionListener(object : MouseMotionAdapter(){
            override fun mouseDragged(e: MouseEvent?) {
                super.mouseDragged(e)
                repeat(2) {
                    drawSelectRect()
                    pt2 = e?.point
                }
            }
        })
    }

    private fun drawSelectRect(){
        with (graphics){
            setXORMode(Color.WHITE)
            rect?.let { r ->
                drawRect(r.x, r.y, r.width, r.height)
                setPaintMode()
            }
        }
    }
}
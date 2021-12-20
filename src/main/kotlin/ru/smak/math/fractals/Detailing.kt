package ru.smak.math.fractals

import ru.smak.ui.painting.CartesianPlane

class Detailing (val plane: CartesianPlane){

    var a= Math.abs(plane.ySegment.first-plane.ySegment.second)

    var iterations: Int = 50
        get() {
            if (a>=1.0) return  50
            else{
                if (a>=0.5&&a<1.0){ return  1000 - Math.round(a * 900).toInt()
                }else {
                    if (a>=0.2&&a<0.5){ return 1000 - Math.round(a * 2000).toInt()
                    }else {
                        if (a>=0.1&&a<0.2){ return 1000 - Math.round(a * 4000).toInt()
                        }else{return 1000 - Math.round(a * 8000).toInt()}
            }}}
        }

}
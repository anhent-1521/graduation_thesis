package com.example.tuananhe.myapplication.screen.edit.crop

interface CropContract {
    interface View : com.example.tuananhe.myapplication.View

    interface Presenter : com.example.tuananhe.myapplication.Presenter {
        fun doEdit(xPos: Int, yPos: Int, cropW: Int, cropH: Int)
    }

}
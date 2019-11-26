package com.example.tuananhe.myapplication.data

import com.example.tuananhe.myapplication.R

data class ItemEdit(val image: Int, val title: String) {

    companion object {
        const val TRIM = "Trim"
        const val REMOVE_MIDDLE = "Remove Middle"
        const val ADD_MUSIC = "Add Music"
        const val ADD_IMAGE = "Add Image"
        const val ADD_INTRO = "Add Intro"
        const val CROP = "Crop"
        const val SPEED = "Speed"
        const val ROTATE = "Rotate"

        val EDITS = arrayListOf(
            ItemEdit(R.drawable.ic_trim, TRIM),
            ItemEdit(R.drawable.ic_remove_middle, REMOVE_MIDDLE),
            ItemEdit(R.drawable.ic_add_music, ADD_MUSIC),
            ItemEdit(R.drawable.ic_add_image, ADD_IMAGE),
            ItemEdit(R.drawable.ic_intro, ADD_INTRO),
            ItemEdit(R.drawable.ic_crop, CROP),
            ItemEdit(R.drawable.ic_speed, SPEED),
            ItemEdit(R.drawable.ic_rotate, ROTATE)
        )
    }
}
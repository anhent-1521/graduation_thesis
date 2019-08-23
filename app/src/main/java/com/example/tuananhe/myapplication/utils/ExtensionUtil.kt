package com.example.tuananhe.myapplication.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

class ExtensionUtil {

    fun View.hideDown(value: Float) {
        this.animate().translationY(value)
            .alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    this@hideDown.visibility = View.GONE
                }
            })
    }

    fun View.showUp() {
        this.visibility = VISIBLE
        this.animate().translationY(0f)
            .alpha(1f)
            .setListener(null)
    }

    fun View.fadeIn() {
        animate()
            .alpha(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    this@fadeIn.visibility = GONE
                }
            })
    }

    fun View.fadeOut() {
        visibility = VISIBLE
        this.animate()
            .alpha(1f)
            .setDuration(300)
            .setListener(null)
    }
}
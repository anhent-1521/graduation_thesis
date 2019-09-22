package com.example.tuananhe.myapplication.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import com.example.tuananhe.myapplication.R
import kotlinx.android.synthetic.main.dialog_common.*

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

    fun Button.onTouchChangeStyle(context: Context, downColor: Int, upColor: Int, downDrawable: Int, upDrawable: Int) {
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextColor(ResourcesCompat.getColor(context.resources, downColor, context.theme))
                    background = context.getDrawable(downDrawable)
                }
                MotionEvent.ACTION_UP -> {
                    setTextColor(ResourcesCompat.getColor(context.resources, upColor, context.theme))
                    background = context.getDrawable(upDrawable)
                }
            }
            false
        }
    }

    fun Button.changePrimaryStyle(context: Context) {
        onTouchChangeStyle(
                context,
                R.color.color_white,
                R.color.colorPrimary,
                R.drawable.bg_dialog_optimistic_press,
                R.drawable.bg_dialog_optimistic_idle
        )
    }

    fun Button.changeDarkerStyle(context: Context) {
        onTouchChangeStyle(
                context,
                R.color.color_white,
                R.color.color_default_text,
                R.drawable.bg_dialog_cancel_press,
                R.drawable.bg_dialog_cancel_idle
        )
    }
}

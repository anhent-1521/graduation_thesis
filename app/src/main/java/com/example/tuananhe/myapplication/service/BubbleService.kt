package com.example.tuananhe.myapplication.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.IBinder
import android.os.Parcelable
import android.support.v4.app.NotificationCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import com.example.tuananhe.myapplication.R

import com.example.tuananhe.myapplication.floating_bubble.floatingview.FloatingViewListener
import com.example.tuananhe.myapplication.floating_bubble.floatingview.FloatingViewManager


/**
 * ChatHead Service
 */
class ChatHeadService : Service(), FloatingViewListener {

    /**
     * FloatingViewManager
     */
    private var mFloatingViewManager: FloatingViewManager? = null

    /**
     * {@inheritDoc}
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // 既にManagerが存在していたら何もしない
        if (mFloatingViewManager != null) {
            return START_STICKY
        }

        val metrics = DisplayMetrics()
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        val inflater = LayoutInflater.from(this)
        val iconView = inflater.inflate(R.layout.widget_chathead, null, false) as ImageView
        iconView.setOnClickListener { Log.d(TAG, "clickkkkkkkkkkk") }

        mFloatingViewManager = FloatingViewManager(this, this)
        mFloatingViewManager!!.setFixedTrashIconImage(R.drawable.ic_trash_fixed)
        mFloatingViewManager!!.setActionTrashIconImage(R.drawable.ic_trash_action)
        mFloatingViewManager!!.setSafeInsetRect(
            intent.getParcelableExtra<Parcelable>(
                EXTRA_CUTOUT_SAFE_AREA
            ) as Rect
        )
        val options = FloatingViewManager.Options()
        options.overMargin = (16 * metrics.density).toInt()
        mFloatingViewManager!!.addViewToWindow(iconView, options)

        // 常駐起動
        startForeground(NOTIFICATION_ID, createNotification(this))

        return START_REDELIVER_INTENT
    }

    /**
     * {@inheritDoc}
     */
    override fun onDestroy() {
        destroy()
        super.onDestroy()
    }

    /**
     * {@inheritDoc}
     */
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * {@inheritDoc}
     */
    override fun onFinishFloatingView() {
        stopSelf()
    }

    /**
     * {@inheritDoc}
     */
    override fun onTouchFinished(isFinishing: Boolean, x: Int, y: Int) {

    }

    /**
     * Viewを破棄します。
     */
    private fun destroy() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager!!.removeAllViewToWindow()
            mFloatingViewManager = null
        }
    }

    companion object {

        /**
         * デバッグログ用のタグ
         */
        private val TAG = "ChatHeadService"

        /**
         * Intent key (Cutout safe area)
         */
        val EXTRA_CUTOUT_SAFE_AREA = "cutout_safe_area"

        /**
         * 通知ID
         */
        private val NOTIFICATION_ID = 9083150

        /**
         * 通知を表示します。
         * クリック時のアクションはありません。
         */
        private fun createNotification(context: Context): Notification {
            val builder = NotificationCompat.Builder(
                context,
                context.getString(R.string.default_floatingview_channel_id)
            )
            builder.setWhen(System.currentTimeMillis())
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.setContentTitle("ok running")
            builder.setContentText("run baby run")
            builder.setOngoing(true)
            builder.priority = NotificationCompat.PRIORITY_MIN
            builder.setCategory(NotificationCompat.CATEGORY_SERVICE)

            return builder.build()
        }
    }
}
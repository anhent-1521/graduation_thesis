package com.example.tuananhe.myapplication.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.support.v4.app.NotificationCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.floating_bubble.circularfloatingactionmenu.FloatingActionMenu
import com.example.tuananhe.myapplication.floating_bubble.floatingview.FloatingView
import com.example.tuananhe.myapplication.floating_bubble.floatingview.FloatingViewListener
import com.example.tuananhe.myapplication.floating_bubble.floatingview.FloatingViewManager


/**
 * ChatHead Service
 */
class BubbleService : Service(), FloatingViewListener, FloatingActionMenu.MenuStateChangeListener {

    /**
     * FloatingViewManager
     */
    private var mFloatingViewManager: FloatingViewManager? = null

    var screenPoint: Point = Point()

    private lateinit var windowManager: WindowManager


    /**
     * {@inheritDoc}
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // 既にManagerが存在していたら何もしない
        if (mFloatingViewManager != null) {
            return START_STICKY
        }

        val metrics = DisplayMetrics()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        windowManager.defaultDisplay.getSize(screenPoint)

        val inflater = LayoutInflater.from(this)
        val iconView = inflater.inflate(R.layout.widget_chathead, null, false) as ImageView
        initFrameOverLay()

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
        options.floatingViewY = screenPoint.y / 2
        val bubble = mFloatingViewManager!!.addViewToWindow(iconView, options)
        bubble.setScreenPoint(screenPoint)
        bubble.setStateChangeListener(this)
        val almostHalfMenuSize = bubble.subActionItems[0].height * 2 + 15
        iconView.setOnClickListener {

            val frame: FrameLayout = iconView.parent as FrameLayout
            val param: WindowManager.LayoutParams = frame.layoutParams as WindowManager.LayoutParams
            val jumpSize = almostHalfMenuSize + frame.height / 2

            if (param.y > screenPoint.y - jumpSize) {
                param.y = screenPoint.y - jumpSize
            }
            if (param.y < jumpSize) {
                param.y = jumpSize
            }
            windowManager.updateViewLayout(frame, param)

            val windowParam: WindowManager.LayoutParams =
                    (iconView.parent as FloatingView).windowLayoutParams

            if (windowParam.x <= 0) {
                bubble.setStartAngle(-70)
                bubble.setEndAngle(70)
            } else {
                bubble.setStartAngle(110)
                bubble.setEndAngle(250)
            }
            bubble.toggle(true)
        }
        // 常駐起動
        startForeground(NOTIFICATION_ID, createNotification(this))

        return START_REDELIVER_INTENT
    }

    private var frameOverlay: FrameLayout? = null
    private var overlayParam: WindowManager.LayoutParams? = null

    private fun initFrameOverLay() {
        frameOverlay = FrameLayout(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            overlayParam = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT)
        } else {
            overlayParam = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT)
        }
        windowManager.addView(frameOverlay, overlayParam)
        frameOverlay?.visibility = View.GONE
        frameOverlay?.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.color_overlay, theme))
        windowManager.updateViewLayout(frameOverlay, overlayParam)
    }

    override fun onMenuOpened(menu: FloatingActionMenu?) {
        frameOverlay?.visibility = View.VISIBLE
        windowManager.updateViewLayout(frameOverlay, overlayParam)
    }

    override fun onMenuClosed(menu: FloatingActionMenu?) {
        frameOverlay?.visibility = View.GONE
        windowManager.updateViewLayout(frameOverlay, overlayParam)
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

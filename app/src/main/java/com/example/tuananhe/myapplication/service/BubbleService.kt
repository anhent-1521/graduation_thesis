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
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.evenBus.Event
import com.example.tuananhe.myapplication.floating_bubble.circularfloatingactionmenu.FloatingActionMenu
import com.example.tuananhe.myapplication.floating_bubble.floatingview.FloatingView
import com.example.tuananhe.myapplication.floating_bubble.floatingview.FloatingViewListener
import com.example.tuananhe.myapplication.floating_bubble.floatingview.FloatingViewManager
import com.example.tuananhe.myapplication.notification.NotificationHelper
import com.example.tuananhe.myapplication.utils.AppUtil
import com.example.tuananhe.myapplication.utils.Constant
import com.example.tuananhe.myapplication.utils.Settings
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * ChatHead Service
 */
class BubbleService : Service(), FloatingViewListener, FloatingActionMenu.MenuStateChangeListener {

    /**
     * FloatingViewManager
     */
    private var mFloatingViewManager: FloatingViewManager? = null

    private var notificationHelper: NotificationHelper? = null

    var screenPoint: Point = Point()

    private lateinit var windowManager: WindowManager
    private var metrics = DisplayMetrics()
    private lateinit var iconView: FrameLayout
    private lateinit var inflater: LayoutInflater

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        EventBus.getDefault().register(this)
    }

    /**
     * {@inheritDoc}
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // 既にManagerが存在していたら何もしない
        if (mFloatingViewManager != null) {
            return START_STICKY
        }

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        windowManager.defaultDisplay.getSize(screenPoint)

        inflater = LayoutInflater.from(this)
        iconView = inflater.inflate(R.layout.widget_chathead, null, false) as FrameLayout
        initFrameOverLay()

        mFloatingViewManager = FloatingViewManager(this, this)
        mFloatingViewManager!!.setFixedTrashIconImage(R.drawable.ic_trash_fixed)
        mFloatingViewManager!!.setActionTrashIconImage(R.drawable.ic_trash_action)
        mFloatingViewManager!!.setSafeInsetRect(
                intent.getParcelableExtra<Parcelable>(
                        EXTRA_CUTOUT_SAFE_AREA
                ) as Rect
        )
        setupBubble(false)
        // 常駐起動
        notificationHelper?.showNormalNotification()

        return START_REDELIVER_INTENT
    }

    private var frameOverlay: FrameLayout? = null
    private lateinit var bubble: FloatingActionMenu
    private var overlayParam: WindowManager.LayoutParams? = null

    private fun initFrameOverLay() {
        frameOverlay = FrameLayout(this)

        overlayParam = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT)
        } else {
            WindowManager.LayoutParams(
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Event) {
        when (event.action) {
            Constant.START_RECORD -> {
                mFloatingViewManager!!.removeAllViewToWindow()
                setupBubble(true)
                mFloatingViewManager!!.onRecordStarted()
            }
            Constant.RECORDING -> {
                mFloatingViewManager!!.onRecording(event.message)
            }
            Constant.PAUSE_RECORD -> {
                mFloatingViewManager!!.onRecordPaused()
                setupBubble(true)
            }
            Constant.RESUME_RECORD -> {
                mFloatingViewManager!!.onRecordResumed()
                setupBubble(true)
            }
            Constant.STOP_RECORD -> {
                mFloatingViewManager!!.removeAllViewToWindow()
                setupBubble(false)
                mFloatingViewManager!!.onRecordStopped()
            }
            Constant.EXIT_RECORD -> {
                mFloatingViewManager!!.finish()
                stopForeground(true)
                stopSelf()
            }
        }
    }

    private fun setupBubble(isStartRecord: Boolean) {
        val options = FloatingViewManager.Options()
        options.overMargin = (16 * metrics.density).toInt()
        options.floatingViewY = screenPoint.y / 2
        bubble = mFloatingViewManager!!.addViewToWindow(iconView, options, isStartRecord)
        bubble.setScreenPoint(screenPoint)
        bubble.setStateChangeListener(this)
        val almostHalfMenuSize = bubble.subActionItems[0].height * 2 + 15
        mFloatingViewManager!!.waitForClose()
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
            mFloatingViewManager?.showFloatingView()
            mFloatingViewManager?.removeFrog()
            mFloatingViewManager?.waitForClose()
            bubble.toggle(true)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onDestroy() {
        destroy()
        EventBus.getDefault().unregister(this)
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
        AppUtil.controlRecord(this, Constant.HIDE_NOTI)
        stopForeground(true)
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

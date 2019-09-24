package com.example.tuananhe.myapplication.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.IBinder
import android.os.Parcelable
import android.support.v4.app.NotificationCompat
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.tuananhe.myapplication.R
import com.example.tuananhe.myapplication.floating_bubble.circularfloatingactionmenu.FloatingActionButton
import com.example.tuananhe.myapplication.floating_bubble.circularfloatingactionmenu.FloatingActionMenu
import com.example.tuananhe.myapplication.floating_bubble.circularfloatingactionmenu.SubActionButton
import com.example.tuananhe.myapplication.floating_bubble.floatingview.FloatingViewListener
import com.example.tuananhe.myapplication.floating_bubble.floatingview.FloatingViewManager




/**
 * ChatHead Service
 */
class BubbleService : Service(), FloatingViewListener {

    /**
     * FloatingViewManager
     */
    private var mFloatingViewManager: FloatingViewManager? = null

    var screenPoint: Point = Point()

    lateinit var windowManager: WindowManager

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
        val iconView = inflater.inflate(R.layout.widget_chathead, null, false) as FrameLayout

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
        val bubbleView = mFloatingViewManager!!.addViewToWindow(iconView, options)
        iconView.setOnClickListener {
            val frame: FrameLayout = iconView.parent as FrameLayout
            val param: WindowManager.LayoutParams = frame.layoutParams as WindowManager.LayoutParams
            if ((screenPoint.y - param.y) < frame.height * 2) {
                param.y = screenPoint.y - frame.height
            }
            if (param.y >= screenPoint.y - frame.height / 10) {
                param.y = screenPoint.y - frame.height
            }
            windowManager.updateViewLayout(frame, param)
        }
        createMenu(bubbleView)

        // 常駐起動
        startForeground(NOTIFICATION_ID, createNotification(this))

        return START_REDELIVER_INTENT
    }

    private fun createMenu(view: View) {

        val itemBuilder = SubActionButton.Builder(baseContext)
        val item1 = ImageView(this)
        item1.setImageResource(R.drawable.ic_rec)
        val button1 = itemBuilder.setContentView(item1).build()

        val item2 = ImageView(this)
        item2.setImageResource(R.drawable.ic_home)
        val button2 = itemBuilder.setContentView(item2).build()

        val item3 = ImageView(this)
        item3.setImageResource(R.drawable.ic_screenshot)
        val button3 = itemBuilder.setContentView(item3).build()

        val item4 = ImageView(this)
        item4.setImageResource(R.drawable.ic_settings)
        val button4 = itemBuilder.setContentView(item4).build()

        val actionMenu = FloatingActionMenu.Builder(this)
            .addSubActionView(button1)
            .addSubActionView(button2)
            .addSubActionView(button3)
            .addSubActionView(button4)
            .attachTo(view)
            .build()
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
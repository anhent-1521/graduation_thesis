package com.example.tuananhe.myapplication.notification

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews

import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import com.example.tuananhe.myapplication.screen.main.HomeActivity
import com.example.tuananhe.myapplication.screen.transparent.TransparentActivity
import com.example.tuananhe.myapplication.service.record.RecordService
import com.example.tuananhe.myapplication.utils.Constant.Companion.EXIT_RECORD
import com.example.tuananhe.myapplication.utils.Constant.Companion.PAUSE_RECORD
import com.example.tuananhe.myapplication.utils.Constant.Companion.RESUME_RECORD
import com.example.tuananhe.myapplication.utils.Constant.Companion.START_RECORD
import com.example.tuananhe.myapplication.utils.Constant.Companion.STOP_RECORD
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import com.example.tuananhe.myapplication.R


class NotificationHelper(private val mService: Service) {

    companion object Type {
        const val NOTIFICATION_ID = 2181997
        const val TYPE_SERVICE = 1
        const val TYPE_ACTIVITY = 2
    }

    private val expandNotificationView: RemoteViews
        get() {
            val notificationView = RemoteViews(mService.packageName, R.layout.layout_normal_notification)

            getRemoteViewClickListener(notificationView)

            return notificationView
        }

    private val expandRecordingNotificationView: RemoteViews
        get() {
            val notificationView = RemoteViews(
                    mService.packageName,
                    R.layout.layout_notification_recording
            )

            getRemoteViewClickListener(notificationView)

            return notificationView
        }

    private val collapseNotificationView: RemoteViews
        get() {
            val notificationView = RemoteViews(
                    mService.packageName,
                    R.layout.layout_normal_notification_collapse
            )

            getRemoteViewClickListener(notificationView)

            return notificationView
        }

    private val collapseRecordingNotificationView: RemoteViews
        get() {
            val notificationView = RemoteViews(
                    mService.packageName,
                    R.layout.layout_notification_recording_collapse
            )

            getRemoteViewClickListener(notificationView)

            return notificationView
        }

    private val expandPauseNotificationView: RemoteViews
        get() {
            val notificationView = RemoteViews(
                    mService.packageName,
                    R.layout.layout_notification_pausing
            )
            getRemoteViewClickListener(notificationView)

            return notificationView
        }

    private val collapsePauseNotificationView: RemoteViews
        get() {
            val notificationView = RemoteViews(
                    mService.packageName,
                    R.layout.layout_notification_pausing_collapse
            )

            getRemoteViewClickListener(notificationView)
            return notificationView
        }

    fun showNormalNotification() {
        mService.startForeground(NOTIFICATION_ID, createNotification())
    }

    fun showRecordingNotification() {
        mService.startForeground(NOTIFICATION_ID, createRecordingNotification())
    }

    fun showPauseNotification() {
        mService.startForeground(NOTIFICATION_ID, createPauseNotification())
    }

    fun cancelNotification() {
        mService.stopForeground(true)
    }

    private fun getRemoteViewClickListener(remoteViews: RemoteViews) {

        var pendingIntent = createPendingIntent(TYPE_ACTIVITY, TransparentActivity::class.java, START_RECORD)
        remoteViews.setOnClickPendingIntent(R.id.content_view, pendingIntent)

        remoteViews.setOnClickPendingIntent(R.id.image_record, pendingIntent)

        pendingIntent = createPendingIntent(TYPE_SERVICE, RecordService::class.java, PAUSE_RECORD)
        remoteViews.setOnClickPendingIntent(R.id.image_pause, pendingIntent)

        pendingIntent = createPendingIntent(TYPE_SERVICE, RecordService::class.java, RESUME_RECORD)
        remoteViews.setOnClickPendingIntent(R.id.image_resume, pendingIntent)

        pendingIntent = createPendingIntent(TYPE_SERVICE, RecordService::class.java, STOP_RECORD)
        remoteViews.setOnClickPendingIntent(R.id.image_stop, pendingIntent)


//        pendingIntent = createPendingIntent(TYPE_ACTIVITY, ProxyActivity::class.java, ACTION_SCREEN_SHOT)
//        remoteViews.setOnClickPendingIntent(R.id.capture_bt, pendingIntent)

        pendingIntent = createPendingIntent(TYPE_ACTIVITY, HomeActivity::class.java, null)
        remoteViews.setOnClickPendingIntent(R.id.image_home, pendingIntent)

        pendingIntent = createPendingIntent(TYPE_ACTIVITY, HomeActivity::class.java, STOP_RECORD)
        remoteViews.setOnClickPendingIntent(R.id.image_setting, pendingIntent)

        pendingIntent = createPendingIntent(TYPE_SERVICE, RecordService::class.java, EXIT_RECORD, true)
        remoteViews.setOnClickPendingIntent(R.id.text_exit, pendingIntent)

    }

    /**
     * Create pending intent for each intent
     */
    private fun createPendingIntent(intentType: Int, destinationClass: Class<*>, action: String?, exit: Boolean = false): PendingIntent? {
        val intent = Intent(mService, destinationClass)
        intent.action = action

        when (intentType) {
            TYPE_ACTIVITY -> return PendingIntent.getActivity(mService, 0, intent, FLAG_UPDATE_CURRENT)
            TYPE_SERVICE -> return PendingIntent.getService(mService, 0, intent,
                   FLAG_UPDATE_CURRENT)
        }

        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = mService.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

    private fun createRecordingNotification(): Notification {
        val builder = NotificationCompat.Builder(mService, channelId)
                .setWhen(System.currentTimeMillis())
                .setCustomContentView(collapseRecordingNotificationView)
                .setCustomBigContentView(expandRecordingNotificationView)
                .setSmallIcon(R.drawable.ic_moon)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
        return builder.build()
    }

    private fun createPauseNotification(): Notification {
        val builder = NotificationCompat.Builder(mService, channelId)
                .setWhen(System.currentTimeMillis())
                .setCustomContentView(collapsePauseNotificationView)
                .setCustomBigContentView(expandPauseNotificationView)
                .setSmallIcon(R.drawable.ic_moon)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
        return builder.build()
    }

    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(mService, channelId)

        builder.setWhen(System.currentTimeMillis())
                .setCustomContentView(collapseNotificationView)
                .setCustomBigContentView(expandNotificationView)
                .setSmallIcon(R.drawable.ic_moon)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setAutoCancel(true).priority = NotificationCompat.PRIORITY_MAX
        return builder.build()
    }

//    /**
//     * Show recording time
//     */
//    private fun showRecodringTime(remoteViews: RemoteViews) {
//        mShowRecordingTimer = Timer()
//        mShowRecordingTimer!!.schedule(object : TimerTask() {
//            override fun run() {
//                if (mRecordingStatus == RECORDING) {
//                    mRecordingTime++
//
//
//                }
//            }
//        }, 0, TimeUnit.SECONDS.toMillis(1))
//    }

}

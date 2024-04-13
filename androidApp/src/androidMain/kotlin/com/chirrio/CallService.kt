package com.chirrio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat


class CallService : Service() {

    lateinit var mPlayer2: MediaPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val customView = RemoteViews(packageName, R.layout.custom_call_notification)

        val notificationIntent = Intent(this, CallActivity::class.java)
        val hungupIntent = Intent(this, NotificationCloseService::class.java)
        val answerIntent = Intent(this, CallActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val hungupPendingIntent =
            PendingIntent.getService(
                this,
                0,
                hungupIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        val answerPendingIntent =
            PendingIntent.getActivity(
                this, 0, answerIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        customView.setOnClickPendingIntent(R.id.btnAnswer, answerPendingIntent)
        customView.setOnClickPendingIntent(R.id.btnDecline, answerPendingIntent)

        mPlayer2 = MediaPlayer.create(this, R.raw.ringing)
        mPlayer2.isLooping = true
        mPlayer2.start()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                "IncomingCall",
                "IncomingCall", NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            val notification = NotificationCompat.Builder(this, "IncomingCall")
            notification.setContentTitle("Today Connect")
            notification.setTicker("Call_STATUS")
            notification.setContentText("IncomingCall")
            notification.setSmallIcon(R.drawable.call)
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setCategory(NotificationCompat.CATEGORY_CALL)
            notification.setVibrate(null)
            notification.setOngoing(true)
            notification.setFullScreenIntent(pendingIntent, true)
            notification.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            notification.setCustomContentView(customView)
            notification.setCustomBigContentView(customView)

            startForeground(1124, notification.build())
        } else {
            val notification = NotificationCompat.Builder(this)
            notification.setContentTitle("Today Connect")
            notification.setTicker("Call_STATUS")
            notification.setContentText("IncomingCall")
            notification.setSmallIcon(R.drawable.call)
            notification.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.call))
            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            notification.setVibrate(null)
            notification.setContentIntent(pendingIntent)
            notification.setOngoing(true)
            notification.setCustomContentView(customView)
            notification.setCustomBigContentView(customView)
            startForeground(1124, notification.build())
        }

        check()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun check() {
        if (CallObject.call) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
            stopSelf()
            mPlayer2.stop()
        }
        Handler().postDelayed({
            check()
        },300)
    }

}

class NotificationCloseService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("dovnd", "1" )
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Close the notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.e("dobvdv", "touched" )
        notificationManager.cancel(1124)

        // Stop the service
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }
}

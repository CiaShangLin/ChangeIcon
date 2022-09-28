package com.shang.changeicon

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat

class ChangeIconService : Service() {

    companion object {
        private const val FOREGROUND_ID = 22921
        private const val CHANNEL_NAME = "ICON"
        private const val CHANNEL_ID = "22921"

        fun start(context: Context) {
            val intent = Intent(context, ChangeIconService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(FOREGROUND_ID, getNotification(baseContext))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        disableComponent(ComponentName(application, "com.shang.changeicon.MainActivity"))
        enableComponent(ComponentName(application, "com.shang.changeicon.Apple"))
        showToast()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
            stopSelf()
        } else {
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun showToast() {
        Toast.makeText(applicationContext, "APP圖標更新中\n等待10秒後即可使用！", Toast.LENGTH_LONG)
            .apply {
                this.view?.findViewById<TextView>(android.R.id.message)?.let { v ->
                    v.gravity = Gravity.CENTER
                }
                setGravity(Gravity.CENTER, 0, 0)
            }.show()
    }

    //起用组件
    private fun enableComponent(componentName: ComponentName) {
        application.packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
    }

    //隐藏组件
    private fun disableComponent(componentName: ComponentName) {
        application.packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
    }

    private fun getNotification(context: Context): Notification {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText("更换ICON中...")
            .setPriority(NotificationCompat.PRIORITY_LOW)

        return notification.build()
    }
}
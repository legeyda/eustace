package com.legeyda.eustace;

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.concurrent.atomic.AtomicReference


class EustaceService: Service() {

    companion object {
        private var instanceHolder = AtomicReference<EustaceService>()
        fun isRunning(): Boolean {
            return null!=instanceHolder.get()
        }
    }

    private var workThread: Thread? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startThread();
        instanceHolder.set(this)
        startForeground(EustaceConstants.NOTIFICATION_ID, createNotification())
        return super.onStartCommand(intent, flags, startId)
    }

    @Synchronized
    private fun startThread() {
        if(null == workThread || !(workThread?.isAlive!!)) {
            workThread = Thread {
                try {
                    while (true) {
                        try {
                            Navigator(EustaceApplication.INSTANCE.settings)
                                .sendCurrentPosition(3 * 1000)
                            if (Thread.currentThread().isInterrupted) {
                                break
                            }
                            Thread.sleep(1 * 1000)
                        } catch (e: InterruptedException) {
                            throw e
                        } catch(e: Exception) {
                            Log.e(javaClass.name, "exception", e)
                            Thread.sleep(10 * 1000)
                        }
                    }
                } catch (e: InterruptedException) {
                    Log.d(javaClass.name, "service thread interrupted")
                }
            }
            workThread?.start()
        }
    }


    private fun createNotification(): Notification {
        createMessagesNotificationChannel(applicationContext)

        val intent = Intent(EustaceApplication.INSTANCE, MainActivity::class.java)
        return NotificationCompat.Builder(applicationContext, EustaceConstants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.logo192)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(applicationContext.getString(R.string.notification_text))
            .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
            .build()
    }

    private fun createMessagesNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.message_channel_name)
            val channel = NotificationChannel(EustaceConstants.NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        instanceHolder.set(null)
        stopThread()
    }

    @Synchronized
    private fun stopThread() {
        if(null != workThread) {
            workThread?.interrupt();
        }
        workThread = null;
    }
}

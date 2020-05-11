package com.legeyda.eustace

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION = "android.intent.action.BOOT_COMPLETED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION) {
            val serviceIntent = Intent(context, SendPositionService::class.java)
            context.startService(serviceIntent)
        }
    }

}
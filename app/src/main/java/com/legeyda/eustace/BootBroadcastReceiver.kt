package com.legeyda.eustace

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        EustaceApplication.INSTANCE.ensureWorking()
    }
}
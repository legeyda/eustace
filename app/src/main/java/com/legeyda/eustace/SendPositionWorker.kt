package com.legeyda.eustace;

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.futures.SettableFuture
import cofm.legeyda.eustace.EustaceSettings
import com.google.common.util.concurrent.ListenableFuture


class SendPositionWorker(context: Context, workerParams: WorkerParameters) :
    ListenableWorker(context, workerParams) {

    override fun startWork(): ListenableFuture<Result> {
        val result: SettableFuture<Result> = SettableFuture.create();
        Thread {
            Navigator(EustaceSettings(EustaceApplication.INSTANCE.applicationContext))
                .sendCurrentPosition(60*1000)
        }.start()
        return result;
    }

}

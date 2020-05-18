package com.legeyda.eustace;

import android.content.Context
import android.util.Log
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
            try {
                Navigator(EustaceSettings(EustaceApplication.INSTANCE.applicationContext))
                    .sendCurrentPosition(10 * 1000)
                result.set(Result.success())
            } catch (e :Exception) {
                Log.e(javaClass.name, "exception", e)
                result.set(Result.failure())
            }
        }.start()
        return result;
    }

}

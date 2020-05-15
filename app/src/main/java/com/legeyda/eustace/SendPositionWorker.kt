package com.legeyda.eustace;

import android.content.Context
import android.preference.PreferenceManager
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.futures.SettableFuture
import com.google.common.util.concurrent.ListenableFuture


class SendPositionWorker(context: Context, workerParams: WorkerParameters) :
    ListenableWorker(context, workerParams) {

    override fun startWork(): ListenableFuture<Result> {
        val result: SettableFuture<Result> = SettableFuture.create();
        Thread(Runnable {
            val preferences =
                PreferenceManager.getDefaultSharedPreferences(EustaceApplication.context)


            // todo get gps and send to alexhq

            result.set(Result.success())
        }).start();
        return result;
    }

}

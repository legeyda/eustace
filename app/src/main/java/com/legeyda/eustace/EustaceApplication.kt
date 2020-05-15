package com.legeyda.eustace

import android.app.Application
import android.content.Context
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

public class EustaceApplication : Application() {

    companion object {
        private val contextHolder: AtomicReference<Context?> = AtomicReference(null);
        val context: Context get() = contextHolder.get()!!
    }

    override fun onCreate() {
        super.onCreate()
        contextHolder.set(applicationContext)
        ensureWorkerScheduled()
    }

    private fun ensureWorkerScheduled() {
        val settings = EustaceSettings(applicationContext)
        if(settings.jobEnabled) {
            if(settings.jobId.isEmpty()) {
                val job = PeriodicWorkRequest.Builder(
                    SendPositionWorker::class.java,
                    settings.jobInterval.toLong(),
                    TimeUnit.MINUTES
                ).build()
                settings.jobId = job.stringId
                WorkManager.getInstance().enqueue(job)
                settings.save()
            }
        } else {
            if(settings.jobId.isNotEmpty()) {
                WorkManager.getInstance().cancelWorkById(UUID.fromString(settings.jobId))
                settings.jobId = "";
                settings.save()
            }
        }
    }
}
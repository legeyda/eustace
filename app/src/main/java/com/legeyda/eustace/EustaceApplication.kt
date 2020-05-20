package com.legeyda.eustace

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import cofm.legeyda.eustace.EustaceSettings
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference


class EustaceApplication : Application() {

    companion object {
        private val contextHolder: AtomicReference<EustaceApplication?> = AtomicReference(null);
        val INSTANCE: EustaceApplication get() = contextHolder.get()!!
    }

    lateinit var settings: EustaceSettings private set

    override fun onCreate() {
        super.onCreate()
        contextHolder.set(this)
        this.settings = EustaceSettings(this.applicationContext)
        this.ensureWorking()
    }

    fun ensureWorking() {
        ensurePeriodicWorkScheduled()
        ensureServiceWorking()
    }

    fun ensurePeriodicWorkScheduled() {
        if (settings.mode != "off") {
            if (settings.workerId.isEmpty()) {
                val job = PeriodicWorkRequest.Builder(
                    SendPositionWorker::class.java,
                    PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                    TimeUnit.MILLISECONDS
                ).build()
                WorkManager.getInstance().enqueue(job)
                settings.workerId = job.stringId
                Log.d(javaClass.name, String.format("ensurePeriodicWorkScheduled: scheduled periodic work id %s", job.stringId))
            }
        } else {
            if (settings.workerId.isNotEmpty()) {
                WorkManager.getInstance().cancelWorkById(UUID.fromString(settings.workerId))
                Log.d(javaClass.name, String.format("ensurePeriodicWorkScheduled: UNscheduled periodic work id %s", settings.workerId))
                settings.workerId = ""
            }
        }
        settings.save()
    }

    fun ensureServiceWorking() {
        if (settings.mode == "foreground") {
            ContextCompat.startForegroundService(this, Intent(this, EustaceService::class.java))
        } else {
            stopService(Intent(this, EustaceService::class.java))
        }
    }

    fun rescheduleWork() {
        reschedulePeriodicWork()
        ensureServiceWorking()
    }

    fun reschedulePeriodicWork() {
        if (settings.mode != "off") {
            if (settings.workerId.isNotEmpty()) {
                WorkManager.getInstance().cancelUniqueWork(settings.workerId);
                Log.d(javaClass.name, String.format("reschedulePeriodicWork: UNscheduled periodic work id %s", settings.workerId))
                settings.workerId = "";
                settings.save()
            }
            val job = PeriodicWorkRequest.Builder(
                SendPositionWorker::class.java,
                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            WorkManager.getInstance().enqueue(job)
            settings.workerId = job.stringId
            Log.d(javaClass.name, String.format("reschedulePeriodicWork: scheduled periodic work id %s", job.stringId))
        } else {
            if (settings.workerId.isNotEmpty()) {
                WorkManager.getInstance().cancelUniqueWork(settings.workerId)
                Log.d(javaClass.name, String.format("reschedulePeriodicWork: UNscheduled periodic work id %s", settings.workerId))
                settings.workerId = ""
            }
        }
        settings.save()
    }


}
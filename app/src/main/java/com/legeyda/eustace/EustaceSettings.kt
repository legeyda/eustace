package com.legeyda.eustace;

import android.content.Context
import android.preference.PreferenceManager
import androidx.work.PeriodicWorkRequest
import java.util.*

fun CharSequence?.orDefault(defaultValue: CharSequence): String {
    return if (this.isNullOrEmpty()) {
        defaultValue.toString()
    } else {
        this.toString()
    }
}

fun CharSequence?.orRandomUuid(): String {
    return if (this.isNullOrEmpty()) {
        UUID.randomUUID().toString()
    } else {
        this.toString()
    }
}

const val MIN_JOB_INTERVAL_MINUTES = (PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS/60/1000).toInt()

public class EustaceSettings(private val context: Context) {
    var serverUrl: String = ""
    var observableId: String = ""
    var jobEnabled: Boolean = true
    var jobInterval: Int = MIN_JOB_INTERVAL_MINUTES
    var jobId: String = ""

    var serviceEnabled: Boolean = false
    var serviceInterval: Int = 5;

    init {
        load()
    }

    fun load() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context);
        serverUrl = prefs.getString("server_url", "").orDefault("http://alexhq.legeyda.com")
        observableId = prefs.getString("observable_id", "").orRandomUuid()
        jobInterval = prefs.getInt("job_interval", MIN_JOB_INTERVAL_MINUTES)
        jobEnabled = prefs.getBoolean("job_enabled", true)
        jobId = prefs.getString("job_id", "").orDefault("")

        serviceEnabled = prefs.getBoolean("service_enabled", false)
        serviceInterval = prefs.getInt("service_interval", 5)
    }

    fun save() {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        if (serverUrl.isNotBlank()) {
            editor.putString("server_url", serverUrl)
        }
        if (observableId.isNotBlank()) {
            editor.putString("observable_id", observableId)
        }
        editor.putBoolean("job_enabled", jobEnabled)
        editor.putInt("job_interval", jobInterval)
        if (jobId.isNotBlank()) {
            editor.putString("job_id", jobId)
        }

        editor.putBoolean("service_enabled", serviceEnabled)
        editor.putInt("service_interval", serviceInterval)

        editor.apply()
    }

    //http://alexhq.legeyda.com

}


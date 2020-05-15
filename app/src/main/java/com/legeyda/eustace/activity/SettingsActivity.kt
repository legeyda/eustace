package com.legeyda.eustace.activity

import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.legeyda.eustace.EustaceSettings
import com.legeyda.eustace.R
import com.legeyda.eustace.SendPositionWorker
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

fun Editable.overwrite(newValue: CharSequence?) {
    this.clear()
    newValue?.let {
        this.append(newValue)
    }
}

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        // todo findViewById<SeekBar>(R.id.job_interval_bar).min = (PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS/1000/60).toInt()
    }

    override fun onResume() {
        super.onResume()
        val settings =
            EustaceSettings(this.applicationContext)
        findViewById<EditText>(R.id.server_url_box).text.overwrite(settings.serverUrl)
        findViewById<EditText>(R.id.observable_id_box).text.overwrite(settings.observableId)
        findViewById<Switch>(R.id.job_enabled).isChecked = settings.jobEnabled
        findViewById<EditText>(R.id.job_interval_box).text.overwrite(settings.jobInterval.toString());
    }

    override fun onPause() {
        super.onPause()

        val settings =
            EustaceSettings(this.applicationContext)
        settings.serverUrl    = findViewById<EditText>(R.id.server_url_box).text.toString()
        settings.observableId = findViewById<EditText>(R.id.observable_id_box).text.toString()
        settings.jobEnabled   = findViewById<Switch>(R.id.job_enabled).isChecked
        settings.jobInterval  = findViewById<EditText>(R.id.job_interval_box).text.toString().toInt()

        if(settings.jobEnabled) {
            if(settings.jobId.isNotEmpty()) {
                WorkManager.getInstance().cancelUniqueWork(settings.jobId);
                settings.jobId = "";
                settings.save()
            }
            val job = PeriodicWorkRequest.Builder(
                SendPositionWorker::class.java,
                settings.jobInterval.toLong(),
                TimeUnit.MINUTES
            ).build()
            settings.jobId = job.stringId
            WorkManager.getInstance().enqueue(job)
        } else {
            if(settings.jobId.isNotEmpty()) {
                WorkManager.getInstance().cancelUniqueWork(settings.jobId)
                settings.jobId = ""
            }
        }

        settings.save()
    }

}

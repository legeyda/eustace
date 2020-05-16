package com.legeyda.eustace

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.view.children
import cofm.legeyda.eustace.EustaceSettings
import kotlinx.android.synthetic.main.activity_main.*

fun Editable.overwrite(newValue: CharSequence?) {
    this.clear()
    newValue?.let {
        this.append(newValue)
    }
}

fun RadioGroup.getSelectedTag(): Any? {
    this.children.forEach { child ->
        if (child is RadioButton) {
            if (child.isChecked()) {
                return child.getTag()
            }
        }
    }
    return null
}

fun RadioGroup.setSelectedTag(tag: Any) {
    this.children.forEach { child ->
        if (child is RadioButton) {
            child.isChecked = (tag == child.getTag())
        }
    }
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        ensureLocationPermissions()
        val settings = EustaceSettings(this.applicationContext)
        findViewById<RadioGroup>(R.id.mode_radio_group).setSelectedTag(settings.mode)
        findViewById<EditText>(R.id.server_url_box).text.overwrite(settings.serverUrl)
        findViewById<EditText>(R.id.observable_id_box).text.overwrite(settings.observableId)
    }

    override fun onPause() {
        super.onPause()
        val settings = EustaceSettings(this.applicationContext)
        settings.mode = findViewById<RadioGroup>(R.id.mode_radio_group).getSelectedTag().toString()
        settings.serverUrl = findViewById<EditText>(R.id.server_url_box).text.toString()
        settings.observableId = findViewById<EditText>(R.id.observable_id_box).text.toString()

        if (settings.dirty) {
            (application as EustaceApplication).rescheduleWork(settings)
            settings.save()
        } else {
            (application as EustaceApplication).ensureWorking(settings)
        }
    }

    fun ensureLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                // TODO: Display additional rationale for the requested permission.
            }
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1
            )
        }
    }


}

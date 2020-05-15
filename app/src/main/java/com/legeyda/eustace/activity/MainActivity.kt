package com.legeyda.eustace.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.legeyda.eustace.EustaceSettings
import com.legeyda.eustace.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Switch>(R.id.service_enabled_switch).setOnCheckedChangeListener {
                buttonView: CompoundButton, isChecked: Boolean ->
            // todo start service
        }

        findViewById<Button>(R.id.settings_button).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val settings = EustaceSettings(this.applicationContext)
        findViewById<Switch>(R.id.service_enabled_switch).isChecked = settings.serviceEnabled
        findViewById<EditText>(R.id.service_interval_box).text.overwrite(settings.serviceInterval.toString());
    }

    override fun onPause() {
        super.onPause()
        val settings = EustaceSettings(this.applicationContext)
        settings.serviceEnabled = findViewById<Switch>(R.id.service_enabled_switch).isChecked
        settings.serviceInterval = findViewById<EditText>(R.id.service_interval_box).text.toString().toInt()
    }

}

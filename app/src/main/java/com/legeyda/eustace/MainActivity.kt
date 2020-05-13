package com.legeyda.eustace

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

fun Editable.overwrite(newValue: CharSequence?) {
    this.clear()
    newValue?.let {
        this.append(newValue)
    }
}

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

const val ALEXHQ_SERVER_URL = "http://alexhq.legeyda.com"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this.applicationContext);
        findViewById<Switch>(R.id.job_enabled).isChecked = prefs.getBoolean("enabled", true)
        findViewById<EditText>(R.id.server_url_box).text.overwrite(
            prefs.getString("server_url", "").orDefault(ALEXHQ_SERVER_URL)
        )
        findViewById<EditText>(R.id.observable_id_box).text.overwrite(
            prefs.getString(
                "observable_id",
                ""
            ).orRandomUuid()
        )
    }

    override fun onPause() {
        super.onPause()

        val editor = PreferenceManager.getDefaultSharedPreferences(this.applicationContext).edit()
        editor.putBoolean("enabled", findViewById<Switch>(R.id.job_enabled).isChecked)
        with(findViewById<EditText>(R.id.server_url_box).text.toString()) {
            if (this.isNotBlank()) {
                editor.putString("server_url", this)
            }
        }
        with(findViewById<EditText>(R.id.observable_id_box).text.toString()) {
            if (this.isNotBlank()) {
                editor.putString("observable_id", this)
            }
        }
        editor.apply()
    }

}

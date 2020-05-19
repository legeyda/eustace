package cofm.legeyda.eustace;

import android.content.Context
import android.preference.PreferenceManager
import com.legeyda.eustace.EustaceConstants
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


class EustaceSettings(private val context: Context) {
    private var oldMode: String = ""
    private var oldServerUrl: String = ""
    private var oldObservableId: String = ""
    private var oldWorkerId: String = ""

    var mode: String = ""
    var serverUrl: String = ""
    var observableId: String = ""
    var workerId: String = ""

    init {
        load()
    }

    fun load() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mode = prefs.getString("mode", "").orDefault("background")
        serverUrl = prefs.getString("server_url", "").orDefault(EustaceConstants.ALEXHQ_SERVER_URL)
        observableId = prefs.getString("observable_id", "").orRandomUuid()
        workerId = prefs.getString("worker_id", "").orDefault("")
        markAsClean()
    }

    private fun markAsClean() {
        oldMode = mode
        oldServerUrl = serverUrl
        oldObservableId = observableId
        oldWorkerId = workerId
    }

    fun save(): Boolean {
        if (!this.dirty) {
            return false
        }

        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        if (oldMode != mode && mode.isNotBlank()) {
            editor.putString("mode", mode)
        }
        if (oldServerUrl != serverUrl && serverUrl.isNotBlank()) {
            editor.putString("server_url", serverUrl)
        }
        if (oldObservableId != observableId && observableId.isNotBlank()) {
            editor.putString("observable_id", observableId)
        }
        if (oldWorkerId != workerId) {
            editor.putString("worker_id", workerId)
        }

        editor.apply()
        markAsClean()
        return true
    }

    val dirty: Boolean
        get() =
            oldMode != mode || oldServerUrl != serverUrl || oldObservableId != observableId || oldWorkerId != workerId

}


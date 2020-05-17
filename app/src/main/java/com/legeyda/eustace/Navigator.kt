package com.legeyda.eustace;

import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.impl.utils.futures.SettableFuture
import cofm.legeyda.eustace.EustaceSettings
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Future


class Navigator(private val settings: EustaceSettings) {


    fun sendCurrentPosition(minTimeMilli: Long): Boolean {
        val futureLocation = Navigator(settings).getCurrentPosition(minTimeMilli)
        try {
            futureLocation.get()
        } catch (e: Throwable) {
            Log.w(javaClass.name, "startWork: error getting position ", e)
            return false
        }

        if (!futureLocation.isDone) {
            return false
        }

        return sendPosition(futureLocation.get())
    }

    private fun sendPosition(location: Location): Boolean {
        val url =
            URL(settings.serverUrl.trimEnd('/') + "/api/v1/observables/" + settings.observableId + "/states")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"

        val data = String.format(
            Locale.ENGLISH,
            """{"lat":%f,"lon":%f}""",
            location.latitude,
            location.longitude
        )

        val os: OutputStream = conn.outputStream
        os.write(data.toByteArray(Charset.forName("UTF-8")))
        os.close()

        return if (conn.responseCode in 200..299) {
            Log.i(javaClass.name, "sendPosition: successfully sent")
            true
        } else {
            Log.e(
                javaClass.name,
                String.format("sendPosition: error sending, response code %d", conn.responseCode)
            )
            false
        }
    }

    fun getCurrentPosition(minTimeMilli: Long): Future<Location> {


        val locationManager = ContextCompat.getSystemService(
            EustaceApplication.INSTANCE.applicationContext,
            LocationManager::class.java
        )

        val result = SettableFuture.create<Location>()

        val bestProviderName = locationManager?.getBestProvider(buildProviderCriteria(), true);
        if (bestProviderName.isNullOrEmpty()) {
            Log.w(javaClass.name, "unable to get provider name")
            result.setException(NullPointerException("GeoPosition.getCurrentPosition: null best provider name"))
            return result
        }


        val handlerThread = HandlerThread("navigator-handler-thread")


        val listener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    result.set(location)
                } else {
                    result.setException(NullPointerException("LocationListener.onLocationChanged: null location"));
                }
                locationManager.removeUpdates(this)
                handlerThread.quit()
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {}
            override fun onProviderDisabled(provider: String?) {}
        }


        handlerThread.start()
        Thread {
            handlerThread.join(minTimeMilli * 3)
            if (handlerThread.isAlive) {
                locationManager.getProviders(true)
                    .filterNot { it == bestProviderName }
                    .forEach {
                        providerName ->
                        try {
                            locationManager.requestLocationUpdates(
                                providerName,
                                minTimeMilli,
                                (0.0).toFloat(),
                                listener,
                                handlerThread.looper
                            )
                        } catch (e: SecurityException) {
                            locationManager.removeUpdates(listener)
                            handlerThread.quit()
                            EustaceApplication.INSTANCE.startActivity(
                                Intent(EustaceApplication.INSTANCE, MainActivity::class.java)
                            )
                            result.setException(e);
                        }
                    }
                handlerThread.join(minTimeMilli * 2)
                if (handlerThread.isAlive) {
                    locationManager.removeUpdates(listener)
                    handlerThread.quit()
                }
            }
        }.start();

        try {
            locationManager.requestLocationUpdates(
                bestProviderName,
                minTimeMilli,
                (0.0).toFloat(),
                listener,
                handlerThread.looper
            )
        } catch (e: SecurityException) {
            EustaceApplication.INSTANCE.startActivity(
                Intent(EustaceApplication.INSTANCE, MainActivity::class.java)
            )
            result.setException(e);
        }

        return result
    }

    private fun buildProviderCriteria(): Criteria {
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isSpeedRequired = false
        criteria.isCostAllowed = true
        return criteria
    }


}

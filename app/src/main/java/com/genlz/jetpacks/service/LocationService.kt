package com.genlz.jetpacks.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.ServiceSettings
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeAddress
import com.amap.api.services.geocoder.RegeocodeQuery
import com.genlz.jetpacks.di.ApplicationScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    @ApplicationScope
    internal lateinit var scope: CoroutineScope

    private val geocodeSearch by lazy {
        ServiceSettings.updatePrivacyShow(this, true, true)
        ServiceSettings.updatePrivacyAgree(this, true)
        GeocodeSearch(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        return object : ILocationService.Stub() {
            override fun requestLocation(lat: Double, lng: Double): String {
                val query = RegeocodeQuery(
                    LatLonPoint(lat, lng),
                    200f,
                    GeocodeSearch.GPS
                )
                val location: RegeocodeAddress = geocodeSearch.getFromLocation(query)
                return location.city
            }
        }
    }

    companion object {
        private const val TAG = "LocationService"
    }
}
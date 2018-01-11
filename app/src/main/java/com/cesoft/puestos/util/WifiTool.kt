package com.cesoft.puestos.util

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import com.cesoft.puestos.models.Wifi
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ccasanova on 28/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class WifiTool(app: Application) {

	private var nRegs = 0
	private val wifi: WifiManager = app.getSystemService(Context.WIFI_SERVICE) as WifiManager
	private val listeners = ArrayList<(ArrayList<Wifi>)->Unit>()
	//private val type = (ArrayList<Wifi>)->Unit)

	//______________________________________________________________________________________________
	fun addListener(listener: (ArrayList<Wifi>)->Unit) {
		listeners.add(listener)
	}
	//______________________________________________________________________________________________
	fun delListener(listener: (ArrayList<Wifi>)->Unit) {
		listeners.remove(listener)
	}

	//______________________________________________________________________________________________
	fun registerWifiReceiver(app: Application) {
		nRegs++
		if(nRegs == 1) {
			//Log.e(TAG, "registerWifiReceiver:-----------------------------------------------")
			app.registerReceiver(wifiStateChangedReceiver, IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION))
			app.registerReceiver(wifiScanAvailableReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
		}
	}
	//______________________________________________________________________________________________
	fun unregisterWifiReceiver(app: Application) {
		nRegs--
		if(nRegs <= 0) {
			app.unregisterReceiver(wifiStateChangedReceiver)
			app.unregisterReceiver(wifiScanAvailableReceiver)
			nRegs = 0
		}
	}

	//______________________________________________________________________________________________
	@Transient val wifiStateChangedReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			val extraWifiState = intent.getIntExtra(
					WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN)
			when(extraWifiState) {
				WifiManager.WIFI_STATE_DISABLED -> Log.e(TAG, "wifiStateChangedReceiver: WIFI_STATE_DISABLED")
				WifiManager.WIFI_STATE_ENABLED -> Log.e(TAG, "wifiStateChangedReceiver: WIFI_STATE_ENABLED")
			}
		}
	}
	//______________________________________________________________________________________________
	@Transient val wifiScanAvailableReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			//Log.e(TAG, "------------------- Scan finish 0: "+wifi.scanResults.size+"  ::: "+Date().time)

			val apinfos = ArrayList<Wifi>()
			for(s in wifi.scanResults) {
				apinfos.add(Wifi(s.BSSID, s.level, s.BSSID, s.SSID, Date().toString(), -1f,-1f))
				//Log.e(TAG, "wifiScanAvailableReceiver:------------"+s.BSSID+" : "+s.SSID+" : "+s.level+" : ")
			}
			for(listener in listeners) {
				//Log.e(TAG, "------------------- Listener "+listener)
				listener(apinfos)
			}

			//Log.e(TAG, "------------------- Scan finish 1: "+wifi.scanResults.size+"  ::: "+Date().time)
		}
	}

	//______________________________________________________________________________________________
	fun scan() {
		//Log.e(TAG, "------------------- Start scan ::: "+Date().time)
		wifi.startScan()
	}

	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = WifiTool::class.java.simpleName
	}
}
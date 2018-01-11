package com.cesoft.puestos.data.fire

import android.graphics.PointF
import com.cesoft.puestos.models.Wifi
import com.cesoft.puestos.util.Locator
import com.cesoft.puestos.util.Log
import com.google.firebase.firestore.*
import java.util.*
import kotlin.collections.HashMap


/**
 * Created by ccasanova on 26/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
object WifiFire {
	private val TAG: String = WifiFire::class.java.simpleName
	private val ROOT_COLLECTION = "ann"

	private val KEY_WIFI = "wifi"
	private val KEY_POSICION = "pos"
	private val KEY_FECHA = "fecha"
	private val KEY_BSSID = "bssid"	// MAC
	private val KEY_SSID = "ssid"	// Wifi Name
	private val KEY_LEVEL = "level"

	//______________________________________________________________________________________________
	fun save(fire: Fire, posWifi: PointF, lista: ArrayList<Wifi>, callback: (Throwable?) -> Unit) {
		if(lista.isEmpty())return

		val doc = HashMap<String, Any>()
		//Latitude must be in the range of [-90, 90]
		//TODO: O no usar GeoPoint sino x e y      O    dividir entre 10 las latitudes
		val pos = GeoPoint(posWifi.y/10.0, posWifi.x.toDouble()/10.0)
		doc.put(KEY_POSICION, pos)
		doc.put(KEY_FECHA, Date())

		val wifis = MutableList(lista.size, {
			val wifi = HashMap<String, Any>()
			wifi.put(KEY_BSSID, lista[it].bssid)
			wifi.put(KEY_SSID, lista[it].ssid)
			wifi.put(KEY_LEVEL, lista[it].level)
			wifi
		})
		doc.put(KEY_WIFI, wifis)

		fire.getCol(ROOT_COLLECTION)
			.add(doc)
			.addOnSuccessListener({ data ->
				Log.e(TAG, "save: OK: "+data)
				callback(null)
			})
			.addOnFailureListener({ e ->
				Log.e(TAG, "save:e:------------------------------------------------------------", e)
				callback(e)
			})
	}

	//______________________________________________________________________________________________
	fun getAllPosicionRT(fire: Fire, callback: (ArrayList<PointF>, Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION)
			.orderBy(KEY_FECHA)
			.addSnapshotListener({ data: QuerySnapshot?, error: FirebaseFirestoreException? ->
				val res = arrayListOf<PointF>()
				if(error == null && data != null) {

					Log.e("CESDATA", "PTO_X,PTO_Y,WI0,WI1,WI2,WI3,WI4,WI5,WI6,WI7,WI8,WI9")

					//TODO: utilizar WifiTool
					data.forEach { doc ->
						val fecha = doc[KEY_FECHA] as Date
						if(fecha.time > 1515067200000) {

							val pos: GeoPoint = doc[KEY_POSICION] as GeoPoint
							val pos1 = PointF(0f,0f)
							pos1.set(PointF(pos.longitude.toFloat()*10, pos.latitude.toFloat()*10))
							res.add(pos1)

							val wifis = doc[KEY_WIFI] as List<HashMap<String, Any>>


							/// Just print for training ANN -----------------------------------------
							var msg = "" + pos1.x + "," + pos1.y
							for(mac in Locator.MACS) {
								val level = wifis
									.firstOrNull { it[KEY_BSSID] == mac }
									?.let { it[KEY_LEVEL] as Long } ?: -99L
								msg += "," + level
							}
							Log.e("CESDATA", msg)
						}
						/// Just print --------------------------------------------------------
					}
					callback(res, null)
				}
				else {
					callback(res, error)
					Log.e(TAG, "getAllRT:e:----------------------------------------------------", error)
				}
			})
	}




}
package com.cesoft.puestos.data.fire

import android.graphics.PointF
import com.cesoft.puestos.models.Wifi
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
	fun save(fire: Fire, lista: ArrayList<Wifi>, callback: (Throwable?) -> Unit) {
		if(lista.isEmpty())return

		val doc = HashMap<String, Any>()
		//Latitude must be in the range of [-90, 90]
		//TODO: O no usar GeoPoint sino x e y      O    dividir entre 10 las latitudes
		doc.put(KEY_POSICION, GeoPoint(lista[0].y.toDouble()/10.0, lista[0].x.toDouble()/10.0))
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
	val MACS = arrayOf(
			"24:1f:a0:dc:7f:ff",
			"58:35:d9:64:58:80",
			"58:35:d9:64:58:83",
			"58:35:d9:64:58:82",
			"58:35:d9:64:58:87",
			"64:d9:89:99:8d:e3",
			"44:e4:d9:00:76:41",
			"44:e4:d9:00:76:40",
			"64:d9:89:c4:0d:63",
			"44:e4:d9:00:76:47"
			//"e2:55:7d:b3:37:90",
			//"c0:62:6b:8e:96:b0",
			//"64:d9:89:99:8d:e7",
			//"58:35:d9:64:5b:10",
			//"64:d9:89:c4:0d:61",
			//"00:12:5f:0b:bc:70"
			)
	fun getAllPosicionRT(fire: Fire, callback: (ArrayList<PointF>, Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION)
			.addSnapshotListener({ data: QuerySnapshot?, error: FirebaseFirestoreException? ->
				val res = arrayListOf<PointF>()
				if(error == null && data != null) {

					Log.e("CESDATA", "PTO_X,PTO_Y,WI0,WI1,WI2,WI3,WI4,WI5,WI6,WI7,WI8,WI9")

					//TODO: utilizar WifiTool
					data.forEach { doc ->
						val fecha = doc[KEY_FECHA] as Date

						val pos: GeoPoint = doc[KEY_POSICION] as GeoPoint
						if(fecha.time < 1514369354770)//1514282864179)
							res.add(PointF(pos.longitude.toFloat(), pos.latitude.toFloat()))
						else
							res.add(PointF(pos.longitude.toFloat()*10, pos.latitude.toFloat()*10))

						//val fecha = doc[KEY_FECHA]
						val wifis = doc[KEY_WIFI] as List<HashMap<String, Any>>

						var msg =""+pos.longitude+", "+pos.latitude
						for(mac in MACS) {
							val level = wifis
								.firstOrNull { it[KEY_BSSID] == mac }
								?.let { it[KEY_LEVEL] as Long }	?: -99L
							msg += "," + level
						}
						Log.e("CESDATA", msg)
						//Log.e("CESDATA", "------"+doc.id)
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
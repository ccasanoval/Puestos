package com.cesoft.puestos.data.fire

import android.graphics.PointF
import com.cesoft.puestos.models.Wifi
import com.cesoft.puestos.util.Log
import com.google.firebase.firestore.*
import java.util.*



/**
 * Created by ccasanova on 26/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
object WifiFire {
	private val TAG: String = WifiFire::class.java.simpleName
	private val ROOT_COLLECTION = "ann"

	fun save(fire: Fire, lista: ArrayList<Wifi>, callback: (Throwable?) -> Unit) {

		if(lista.isEmpty())return

		val doc = HashMap<String, Any>()
		doc.put("pos", GeoPoint(lista[0].y.toDouble(), lista[0].x.toDouble()))
		doc.put("fecha", Date())

		val wifis = MutableList(lista.size, {
			val wifi = HashMap<String, Any>()
			wifi.put("bssid", lista[it].bssid)
			wifi.put("ssid", lista[it].ssid)
			wifi.put("level", lista[it].level)
			wifi
		})
		doc.put("wifi", wifis)


		fire.getCol(ROOT_COLLECTION)
			.add(doc)
			//.set(docData)
			.addOnSuccessListener({ data ->
				Log.e(TAG, "save: DocumentSnapshot successfully written!")
				callback(null)
			})
			.addOnFailureListener({
				e -> Log.e(TAG, "save:e:-----------------------------------", e)
				callback(e)
			})
	}

	//______________________________________________________________________________________________
	fun getAllRT(fire: Fire, callback: (ArrayList<PointF>, Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION)
			.addSnapshotListener({ data: QuerySnapshot?, error: FirebaseFirestoreException? ->
				val res = arrayListOf<PointF>()
				if(error == null && data != null) {
					data.forEach { doc ->

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
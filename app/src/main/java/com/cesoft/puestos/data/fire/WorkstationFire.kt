package com.cesoft.puestos.data.fire

import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.util.Log
import com.google.firebase.firestore.GeoPoint


/**
 * Created by ccasanova on 12/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: GeoFire queries near location : Aun no implementado en firestore
// https://stackoverflow.com/questions/46553682/is-there-a-way-to-use-geofire-with-firestore
// https://github.com/drfonfon/android-geohash
object WorkstationFire {
	private val TAG: String = WorkstationFire::class.java.simpleName
	private val ROOT_COLLECTION = "workstations"
	private val POSITION_FIELD = "position"

	//______________________________________________________________________________________________
	fun getAll(fire: Fire, callback: (ArrayList<Workstation>, Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION)
			.get()
			.addOnCompleteListener({ task ->
				val res = arrayListOf<Workstation>()
				if(task.isSuccessful) {
					for(doc in task.result) {
						val puesto = fire.translate(doc, Workstation::class.java) as Workstation?
						if(puesto != null) {
							val pos: GeoPoint = doc.get(POSITION_FIELD) as GeoPoint
							puesto.x = pos.longitude.toFloat()
							puesto.y = pos.latitude.toFloat()
							res.add(puesto)
						}
					}
					callback(res, null)
				}
				else {
					Log.e(TAG, "loadComicList:Firebase:e:------------------------ ", task.exception)
					callback(res, task.exception)
				}
			})
	}
}
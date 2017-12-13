package com.cesoft.puestos.data.fire

import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.Log
import com.google.firebase.firestore.GeoPoint


/**
 * Created by ccasanova on 12/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
object WorkstationFire {
	private val TAG: String = WorkstationFire::class.java.simpleName
	private val ROOT_COLLECTION = "puestos"

	//______________________________________________________________________________________________
	fun getAll(fire: Fire, callback: (ArrayList<Workstation>, Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION)
			.get()
			.addOnCompleteListener({ task ->
				val res = arrayListOf<Workstation>()
				if(task.isSuccessful) {
					for(doc in task.result) {
						val puesto = fire.translate(doc, Workstation::class.java) as Workstation
						val pos: GeoPoint = doc.get("posicion") as GeoPoint
						puesto.x = pos.longitude.toFloat()
						puesto.y = pos.latitude.toFloat()
						res.add(puesto)
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
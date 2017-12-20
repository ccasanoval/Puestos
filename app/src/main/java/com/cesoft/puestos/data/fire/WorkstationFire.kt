package com.cesoft.puestos.data.fire

import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.cesoft.puestos.models.User



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

	private val FIELD_IDOWNER = "idOwner"
	private val FIELD_IDUSER = "idUser"
	private val FIELD_NAME = "name"
	private val FIELD_STATUS = "status"

	//______________________________________________________________________________________________
	/*fun getAll(fire: Fire, callback: (ArrayList<Workstation>, Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION)
			.get()
			.addOnCompleteListener({ task ->
				val res = arrayListOf<Workstation>()
				if(task.isSuccessful) {
					for(doc in task.result) {
						val puesto = createPuestoHelper(fire, doc)
						if(puesto != null) res.add(puesto)
					}
					callback(res, null)
				}
				else {
					callback(res, task.exception)
					Log.e(TAG, "getAll:e:------------------------------------------------------", task.exception)
				}
			})
	}*/
	//______________________________________________________________________________________________
	fun getAllRT(fire: Fire, callback: (ArrayList<Workstation>, Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION)
			.addSnapshotListener({ data: QuerySnapshot?, error: FirebaseFirestoreException? ->
				val res = arrayListOf<Workstation>()
				if(error == null && data != null) {
					data.forEach { doc ->
						val puesto = createHelper(fire, doc)
						if(puesto != null) res.add(puesto)
					}
					callback(res, null)
				}
				else {
					callback(res, error)
					Log.e(TAG, "getAllRT:e:----------------------------------------------------", error)
				}
			})
	}
	//______________________________________________________________________________________________
	fun createHelper(fire: Fire, doc: DocumentSnapshot): Workstation? {
		val puesto = fire.translate(doc, Workstation::class.java) as Workstation?
		if(puesto != null) {
			val pos: GeoPoint = doc.get(POSITION_FIELD) as GeoPoint
			return puesto.copy(doc.id, pos.longitude.toFloat(), pos.latitude.toFloat())
		}
		return null
	}

	//______________________________________________________________________________________________
	// Find Workstation by Owner
	/*fun getByOwnerOnlyRT(fire: Fire, idUser: String, callback: (Workstation?, Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION)
			.whereEqualTo(FIELD_IDOWNER, idUser)
			.whereEqualTo(FIELD_IDUSER, "")
			.addSnapshotListener({ data: QuerySnapshot?, error: FirebaseFirestoreException? ->
				if(error == null && data != null)  {
					data.forEach { doc ->
						val puesto = createPuestoHelper(fire, doc)
						callback(puesto, null)
						return@forEach
					}
				}
				else
					callback(null, error)
			})
	}*/
	//______________________________________________________________________________________________
	// Find Workstation by Owner
	fun getByOwnerRT(fire: Fire, idUser: String, callback: (Workstation?, Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION)
			.whereEqualTo(FIELD_IDOWNER, idUser)
			.addSnapshotListener({ data: QuerySnapshot?, error: FirebaseFirestoreException? ->
				if(error == null && data != null)  {
					data.forEach { doc ->
						val puesto = createHelper(fire, doc)
						callback(puesto, null)
						return@forEach
					}
				}
				else
					callback(null, error)
			})
	}
	//______________________________________________________________________________________________
	// Find Workstation by User
	fun getByUser(fire: Fire, idUser: String, callback: (Workstation?, Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION)
			.whereEqualTo(FIELD_IDUSER, idUser)
			.get().addOnCompleteListener({ task: Task<QuerySnapshot> ->
				if(task.isSuccessful)  {
					task.result.forEach { doc ->
						val puesto = createHelper(fire, doc)
						callback(puesto, null)
						return@forEach
					}
					callback(null, null)
				}
				else
					callback(null, task.exception)
			})
	}
	//______________________________________________________________________________________________
	// Find Workstation by User
	fun getByUserRT(fire: Fire, idUser: String, callback: (Workstation?, Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION)
			.whereEqualTo(FIELD_IDUSER, idUser)
			.addSnapshotListener({ data: QuerySnapshot?, error: FirebaseFirestoreException? ->
				if(error == null && data != null)  {
					data.forEach { doc ->
						val puesto = createHelper(fire, doc)
						callback(puesto, null)
						return@forEach
					}
				}
				else
					callback(null, error)
		})
	}

	//______________________________________________________________________________________________
	fun reserve(fire: Fire, id: String, idUser: String, callback: (Throwable?) -> Unit) {
		Log.e(TAG, "reserve:-----------------------------1---------"+id+", "+idUser)

		val puesto = fire.getCol(ROOT_COLLECTION).document(id)

		getByUser(fire, idUser, { elpuesto, error ->
			if(error != null) {
				Log.e(TAG, "reserve:getByUser:e: -------------------------------------------"+puesto, error)
				callback(Exception())
			}
			else if(elpuesto != null) {
				Log.e(TAG, "reserve:getByUser:e: ------------------------------- El usuario ya ocupa otro puesto: " + elpuesto)
				callback(Exception())
			}
			else {
				Log.e(TAG, "reserve:------------3--------------------------"+id+", "+idUser)
				fire.runTransaction(Transaction.Function { transaction ->
					Log.e(TAG, "reserve:-------------------------2-------------"+id+", "+idUser)

					val snapshot = transaction.get(puesto)
					val status = snapshot.getString(FIELD_STATUS)!!
					if(status == Workstation.Status.Free.name) {
						transaction.update(puesto, FIELD_STATUS, Workstation.Status.Occupied.name)
						transaction.update(puesto, FIELD_IDUSER, idUser)
						Log.e(TAG, "reserve ok------------>>"+FIELD_STATUS+"="+Workstation.Status.Occupied.name+" BY "+FIELD_IDUSER+":"+idUser)
					}
					else {
						Log.e(TAG, "reserve:getByUser:e: -----------------------------------STAT: "+status)
						throw FirebaseFirestoreException("Error", FirebaseFirestoreException.Code.ABORTED)
					}
				})
				.addOnSuccessListener {	callback(null) }
				.addOnFailureListener {	error -> callback(error) }
			}
		})
	}

	//______________________________________________________________________________________________
	fun vacate(fire: Fire, id: String, callback: (Throwable?) -> Unit) {
		val puesto = fire.getCol(ROOT_COLLECTION).document(id)
		Log.e(TAG, "vacate:--------------------0----------------------------------"+puesto.id)

		fire.runTransaction(Transaction.Function { transaction ->
			val snapshot = transaction.get(puesto)
			val status = snapshot.getString(FIELD_STATUS)!!
			//val idUserAct = snapshot.getString(FIELD_IDUSER)!!
			if(status == Workstation.Status.Occupied.name) {// && idUser == idUserAct) {
				transaction.update(puesto, FIELD_STATUS, Workstation.Status.Free.name)
				transaction.update(puesto, FIELD_IDUSER, User.NONE)
			}
			else {
				Log.e(TAG, "vacate:getByUser:e: -----------------------------------STAT: "+status)
				throw FirebaseFirestoreException("ocupar: ID "+id+", Status "+status, FirebaseFirestoreException.Code.ABORTED)
			}
		})
		.addOnSuccessListener { callback(null) }
		.addOnFailureListener { error -> callback(error) }
	}
	//______________________________________________________________________________________________
	fun guardar(fire: Fire, id: String, name: String, callback: (Throwable?) -> Unit) {
		fire.getCol(ROOT_COLLECTION).document(id)
			.update(FIELD_NAME, name)
			.addOnSuccessListener({ callback(null) })
			.addOnFailureListener({ error -> callback(error)})
	}
}
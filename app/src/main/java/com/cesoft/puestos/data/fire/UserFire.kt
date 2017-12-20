package com.cesoft.puestos.data.fire

import com.cesoft.puestos.models.User
import com.cesoft.puestos.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

/**
 * Created by ccasanova on 01/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
object UserFire {
	private val TAG: String = User::class.java.simpleName
	private val ROOT_COLLECTION = "users"

	//______________________________________________________________________________________________
	/*fun get(fire: Fire, email: String, callback: (User, Throwable?) -> Unit) {
		fire.getDoc(ROOT_COLLECTION, email)
			.get().addOnCompleteListener({ task ->
				if(task.isSuccessful) {
					val user = fire.translate(task.result, User::class.java) as User
					callback(user, null)
				}
				else {
					Log.e(TAG, "get:e:----------------------------------------------------------", task.exception)
					callback(User(), task.exception)
				}
			})
	}*/
	//______________________________________________________________________________________________
	fun getRT(fire: Fire, email: String, callback: (User, Throwable?) -> Unit) {
		fire.getDoc(ROOT_COLLECTION, email)
			.addSnapshotListener({ doc: DocumentSnapshot?, error: FirebaseFirestoreException? ->
				if(error == null && doc != null) {
					val user = fire.translate(doc, User::class.java) as User
					callback(user.copy(doc.id), null)
				}
				else {
					Log.e(TAG, "getRT:e:-------------------------------------------------------", error)
					callback(User(), error)
				}
			})
	}
}
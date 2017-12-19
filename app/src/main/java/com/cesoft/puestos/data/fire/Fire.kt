package com.cesoft.puestos.data.fire

import com.cesoft.puestos.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.gson.Gson

/**
 * Created by ccasanova on 01/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class Fire {
	private val db = FirebaseFirestore.getInstance()

	//______________________________________________________________________________________________
	fun getCol(collection: String): CollectionReference
			= db.collection(collection)
	//______________________________________________________________________________________________
	fun getDoc(collection: String, document: String): DocumentReference
			= db.collection(collection).document(document)

	//______________________________________________________________________________________________
	fun translate(res: DocumentSnapshot, clase: Class<*>): Any? {
		try {
			return res.toObject(clase)
		}
		catch(e: Exception) {
			Log.e("Fire", "translate:e:----------------------------------------------------"+res.data,e)
		}
		return null
	}

	//______________________________________________________________________________________________
	fun <TResult> runTransaction(t: Transaction.Function<TResult>): Task<TResult> {
		return db.runTransaction(t)
	}
}

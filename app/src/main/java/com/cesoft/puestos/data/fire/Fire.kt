package com.cesoft.puestos.data.fire

import com.cesoft.puestos.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

/**
 * Created by ccasanova on 01/12/2017
 */
class Fire {
	//private val auth = FirebaseAuth.getInstance()
	//private lateinit var authListener: FirebaseAuth.AuthStateListener

	private val db = FirebaseFirestore.getInstance()
	//private var gson = Gson()

	fun getCol(collection: String): CollectionReference
			= db.collection(collection)
	fun getDoc(collection: String, document: String): DocumentReference
			= db.collection(collection).document(document)

	//______________________________________________________________________________________________
	fun translate(res: DocumentSnapshot, clase: Class<*>): Any? {
		//val json = gson.toJsonTree(res.data)
		//val objeto = gson.fromJson(json, clase)
		try {
			//if(res != null)
			return res.toObject(clase)
		}
		catch(e: Exception) {
			Log.e("Fire", "translate:e:----------------------------------------------------",e)
		}
		return null
	}
}

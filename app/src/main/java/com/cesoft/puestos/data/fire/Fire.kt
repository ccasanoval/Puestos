package com.cesoft.puestos.data.fire

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

	fun getRef(collection: String, document: String): DocumentReference
			= db.collection(collection).document(document)

	fun translate(res: DocumentSnapshot, clase: Class<*>): Any {
		//val json = gson.toJsonTree(res.data)
		//val objeto = gson.fromJson(json, clase)
		return res.toObject(clase)
	}
}

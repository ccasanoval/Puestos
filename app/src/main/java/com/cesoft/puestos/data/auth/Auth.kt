package com.cesoft.puestos.data.auth

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

//TODO: DI
/**
 * Created by ccasanova on 30/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class Auth private constructor() {

	private val auth: FirebaseAuth = FirebaseAuth.getInstance()

	//______________________________________________________________________________________________
	fun isLogedIn(): Boolean = auth.currentUser != null
	fun isNotLogedIn(): Boolean = !isLogedIn()
	fun getEmail(): String? = auth.currentUser?.email

	//______________________________________________________________________________________________
	fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {//(listener: ()->Unit) {
		auth.addAuthStateListener(listener)
	}
	//______________________________________________________________________________________________
	fun delAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
		auth.removeAuthStateListener(listener)
	}

	//______________________________________________________________________________________________
	fun login(email: String, clave: String, callback: (Auth, Exception?)->Unit) { //TODO: listener: com.google.android.gms.tasks.OnCompleteListener<Auth>) {
		auth.signInWithEmailAndPassword(email, clave)
			.addOnCompleteListener { auth2 ->
				callback(this, auth2.exception)
			}
//			.addOnCompleteListener(object : OnCompleteListener<AuthResult> {
//				override fun onComplete(p0: Task<AuthResult>) {	}
//			})
	}


	//______________________________________________________________________________________________
	fun logout() {
		auth.signOut()
	}

	//______________________________________________________________________________________________
	companion object {
		fun getInstance(app: Application): Auth {
			FirebaseApp.initializeApp(app)
			return Auth()
		}
	}
}
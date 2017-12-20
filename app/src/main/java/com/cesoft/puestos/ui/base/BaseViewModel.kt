package com.cesoft.puestos.ui.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

import com.cesoft.puestos.App
import com.cesoft.puestos.data.auth.Auth
import com.cesoft.puestos.data.fire.UserFire
import com.cesoft.puestos.data.fire.WorkstationFire
import com.cesoft.puestos.models.User
import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.util.Log
import com.google.firebase.auth.FirebaseAuth


/**
 * Created by ccasanova on 20/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class BaseViewModel(app: Application) : AndroidViewModel(app) {

	private var authListener: FirebaseAuth.AuthStateListener? = null

	/*val user = getApplication<App>().user
	val wsOwn = getApplication<App>().wsOwn
	val wsUse = getApplication<App>().wsUse*/


	//______________________________________________________________________________________________
	fun onCreate(callback: (notLogedIn:Boolean)->Unit) {
		val auth: Auth = getApplication<App>().auth
		authListener = FirebaseAuth.AuthStateListener {
			if(auth.isLogedIn()) {
				iniUserData(auth)
			}
			callback(auth.isNotLogedIn())
		}
		auth.addAuthStateListener(authListener!!)
		Log.e(TAG, "onCreate:--------------------------------------")
	}
	//______________________________________________________________________________________________
	fun onDestroy() {
		Log.e(TAG, "onDestroy:--------------------------------------")
		if(authListener != null)
			getApplication<App>().auth.delAuthStateListener(authListener!!)
		authListener = null
	}

	//______________________________________________________________________________________________
	private fun iniUserData(auth: Auth) {
		Log.e(TAG, "iniUserData:--------------------------------------------------")

		val app = getApplication<App>()
		if(app.user.value == null) {
			if(auth.getEmail() != null) {
				// USR
				UserFire.getRT(app.fire, auth.getEmail().toString(), { usr: User, error ->
					if(error == null) {
						app.user.value = usr	//igualmente = app.user.value = usr
					}
					else {
						Log.e(TAG, "iniUserData:userFire.get:e:-------------------" + auth.getEmail().toString(), error)
					}
				})
				// WORKSTATIONS
				WorkstationFire.getByOwnerRT(app.fire, auth.getEmail().toString(), { ws: Workstation?, error ->
					if(error == null) {
						app.wsOwn.value = ws
					}
					else {
						Log.e(TAG, "iniUserData:getByOwner:e:-------------------", error)
					}
				})
				WorkstationFire.getByUserRT(app.fire, auth.getEmail().toString(), { ws: Workstation?, error ->
					if(error == null) {
						app.wsUse.value = ws
					}
					else {
						Log.e(TAG, "iniUserData:getByUser:e:---------------------------------", error)
					}
				})
			}
		}
	}


	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = BaseViewModel::class.java.simpleName
	}
}
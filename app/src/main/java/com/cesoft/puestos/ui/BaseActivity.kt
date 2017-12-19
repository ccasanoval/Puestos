package com.cesoft.puestos.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.cesoft.puestos.App
import com.cesoft.puestos.util.Log
import com.cesoft.puestos.data.auth.Auth
import com.cesoft.puestos.data.fire.UserFire
import com.cesoft.puestos.data.fire.WorkstationFire
import com.cesoft.puestos.models.User
import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by ccasanova on 30/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
abstract class BaseActivity: AppCompatActivity() {

	//______________________________________________________________________________________________
	private var authListener = FirebaseAuth.AuthStateListener {
		val auth: Auth = (application as App).auth
		if(auth.isNotLogedIn()) {
			toLoginActivity()
		}
		else {
			Log.e(TAG, "authListener:-------------------------------- AUTH EMAIL:" + (application as App).auth.getEmail())
			iniUserData(auth)
		}
	}
	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
		super.onCreate(savedInstanceState, persistentState)
		onCreate(savedInstanceState)
	}
	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val auth: Auth = (application as App).auth
		auth.addAuthStateListener(authListener)
		if(auth.isNotLogedIn()) {
			toLoginActivity()
		}
//		else {
//			Log.e(TAG, "onCreate:----------------------USR:"+auth.getEmail())
//			iniUserData(auth)
//		}
	}
	//______________________________________________________________________________________________
	override fun onDestroy() {
		super.onDestroy()
		Log.e(TAG, "onDestroy:--------------------------------------")
		(application as App).auth.delAuthStateListener(authListener)
	}

	//______________________________________________________________________________________________
	private fun toLoginActivity() {
		val intent = Intent(this, LoginActivity::class.java)
		startActivity(intent)
		finish()
	}

	//______________________________________________________________________________________________
	private fun iniUserData(auth: Auth) {
		//Log.e(TAG, "iniUserData:---------------------USR:" + (application as App).user)
		if((application as App).user == null) {
			//Log.e(TAG, "iniUserData:-------------------EMAIL: " + auth.getEmail())
			if(auth.getEmail() != null) {
				// USR
				UserFire.get((application as App).fire, auth.getEmail().toString(), { user: User, error ->
					if(error == null) {
						(application as App).user = user
						onUser(user)
						Log.e(TAG, "iniUserData:userFire.get:-------------------" + auth.getEmail().toString())
					}
					else {
						Log.e(TAG, "iniUserData:userFire.get:e:-------------------" + auth.getEmail().toString(), error)
					}
				})
				// WORKSTATIONS
				WorkstationFire.getByOwner((application as App).fire, auth.getEmail().toString(), { ws: Workstation?, error ->
					if(error == null) {
						(application as App).wsOwn = ws
						onWorkstationOwn(ws)
						Log.e(TAG, "iniUserData:getByOwner:-------------------"+ws)
					}
					else {
						Log.e(TAG, "iniUserData:getByOwner:e:-------------------", error)
					}
				})
				WorkstationFire.getByUserRT((application as App).fire, auth.getEmail().toString(), { ws: Workstation?, error ->
					if(error == null) {
						(application as App).wsUse = ws
						onWorkstationUse(ws)
						Log.e(TAG, "iniUserData:getByUser:----------------------------------"+ws)
					}
					else {
						Log.e(TAG, "iniUserData:getByUser:e:---------------------------------", error)
					}
				})
			}
		}
	}

	//______________________________________________________________________________________________
	//open
	abstract protected fun onUser(user: User)
	//______________________________________________________________________________________________
	abstract protected fun onWorkstationOwn(wsOwn: Workstation?)
	//______________________________________________________________________________________________
	abstract protected fun onWorkstationUse(wsUse: Workstation?)

	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = BaseActivity::class.java.simpleName
	}
}
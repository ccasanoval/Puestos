package com.cesoft.puestos.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.cesoft.puestos.App
import com.cesoft.puestos.util.Log
import com.cesoft.puestos.data.auth.Auth
import com.cesoft.puestos.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by ccasanova on 30/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
//open
abstract class BaseActivity: AppCompatActivity() {

	//______________________________________________________________________________________________
	private var authListener = FirebaseAuth.AuthStateListener {
		if((application as App).auth.isNotLogedIn())
			toLoginActivity()
		else
			Log.e("BaseActivity", "onCreate:-2---------------------USR:"+(application as App).auth.getEmail())
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
		else {
			Log.e("BaseActivity", "onCreate:----------------------USR:"+auth.getEmail())
		}
	}
	//______________________________________________________________________________________________
	override fun onDestroy() {
		super.onDestroy()
		Log.e("BaseActivity", "onDestroy:--------------------------------------")
		(application as App).auth.delAuthStateListener(authListener)
	}

	//______________________________________________________________________________________________
	private fun toLoginActivity() {
		val intent = Intent(this, LoginActivity::class.java)
		startActivity(intent)
		finish()
	}
}
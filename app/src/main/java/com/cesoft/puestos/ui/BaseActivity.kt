package com.cesoft.puestos.ui

import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.cesoft.puestos.App
import com.cesoft.puestos.Log
import com.cesoft.puestos.ui.login.LoginActivity

/**
 * Created by ccasanova on 30/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
open
abstract class BaseActivity: AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
		super.onCreate(savedInstanceState, persistentState)
		onCreate(savedInstanceState)
	}
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val fireAuth: FirebaseAuth = (application as App).fireAuth
		fireAuth.addAuthStateListener { auth ->
			if(auth.currentUser == null)
				toLoginActivity()
			else
				Log.e("BaseActivity", "onCreate:-2---------------------USR:"+fireAuth.currentUser!!.email)
		}
		if(fireAuth.currentUser == null) {// || fireAuth.currentUser!!.email == null) {
			toLoginActivity()
		}
		else {
			Log.e("BaseActivity", "onCreate:----------------------USR:"+fireAuth.currentUser!!.email)
		}
	}

	//______________________________________________________________________________________________
	private fun toLoginActivity() {
		val intent = Intent(this, LoginActivity::class.java)
		startActivity(intent)
		finish()
	}
}
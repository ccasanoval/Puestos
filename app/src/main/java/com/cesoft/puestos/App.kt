package com.cesoft.puestos

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by ccasanova on 30/11/2017
 */
class App : Application() {
	lateinit var fireAuth: FirebaseAuth

	override fun onCreate() {
		super.onCreate()
		FirebaseApp.initializeApp(this)
		fireAuth = FirebaseAuth.getInstance()
	}
}
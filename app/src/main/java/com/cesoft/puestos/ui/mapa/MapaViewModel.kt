package com.cesoft.puestos.ui.mapa

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.cesoft.puestos.App
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class MapaViewModel(app: Application) : AndroidViewModel(app) {
	private val fireAuth: FirebaseAuth = getApplication<App>().fireAuth

	val usuario = MutableLiveData<String>()//<List<Boolean>>? = null

	init {
		usuario.value = fireAuth.currentUser?.email //displayName
	}

	fun logout() { fireAuth.signOut() }
}
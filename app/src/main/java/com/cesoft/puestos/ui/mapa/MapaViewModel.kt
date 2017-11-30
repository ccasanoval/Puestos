package com.cesoft.puestos.ui.mapa

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.cesoft.puestos.App
import com.cesoft.puestos.data.auth.Auth

/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class MapaViewModel(app: Application) : AndroidViewModel(app) {
	private val auth: Auth = getApplication<App>().auth

	val usuario = MutableLiveData<String>()//<List<Boolean>>? = null

	init {
		usuario.value = auth.getEmail()
	}

	fun logout() { auth.logout() }
}
package com.cesoft.puestos.ui.mapa

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.graphics.PointF
import com.cesoft.puestos.App
import com.cesoft.puestos.Log
import com.cesoft.puestos.data.auth.Auth
import com.cesoft.puestos.data.fire.Fire
import com.cesoft.puestos.data.fire.UserFire
import com.cesoft.puestos.util.Plane
import com.cesoft.puestos.models.User
import com.cesoft.puestos.models.Workstation

/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class MapaViewModel(app: Application) : AndroidViewModel(app) {
	private val auth: Auth = getApplication<App>().auth

	val usuario = MutableLiveData<String>()//<List<Boolean>>? = null
	val puestos = MutableLiveData<List<Workstation>>()
	val plane = Plane(getApplication())
	//val markers = MutableLiveData<List<Workstation>>()

	//______________________________________________________________________________________________
	init {
		//usuario.value = auth.getEmail()
		puestos.value = listOf()
		val fire = Fire()
		val userFire = UserFire()
		if(auth.getEmail() != null)
		userFire.get(fire, auth.getEmail().toString(), { user: User, error ->
			if(error == null) {
				usuario.value = user.name +" : "+user.type
			}
			else {
				usuario.value = auth.getEmail()
				Log.e(TAG, "init:userFire.get:e:--------------------"+auth.getEmail().toString(), error)
			}
		})
	}

	//______________________________________________________________________________________________
	fun logout() { auth.logout() }

	//______________________________________________________________________________________________
	var ini: PointF? = null
	var end: PointF? = null
	fun punto(pto: PointF) {
		if(pto.x < 0 || pto.x >= 100 || pto.y < 0 || pto.y >= 100) return
		if(ini == null) {
			ini = pto
		}
		else {
			end = pto
			//plane.isReady.observe(getApplication<App>(), Observer<Boolean>{
			//	isReady -> if(isReady != null && isReady)
			Log.e(TAG, "CALC: ------------"+ini!!.x+", "+ini!!.y+"  /  "+end!!.x+", "+end!!.y)
			plane.calc(ini!!.x, ini!!.y, end!!.x, end!!.y)
			//})
		}
	}

	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = MapaViewModel::class.java.simpleName
	}
}
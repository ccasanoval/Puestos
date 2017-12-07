package com.cesoft.puestos.ui.mapa

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.graphics.Point
import android.graphics.PointF
import com.cesoft.puestos.App
import com.cesoft.puestos.Log
import com.cesoft.puestos.R
import com.cesoft.puestos.data.auth.Auth
import com.cesoft.puestos.data.fire.Fire
import com.cesoft.puestos.data.fire.UserFire
import com.cesoft.puestos.util.Plane
import com.cesoft.puestos.models.User
import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.ui.CesImgView

/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class MapaViewModel(app: Application) : AndroidViewModel(app) {
	private val auth: Auth = getApplication<App>().auth

	val mensaje = MutableLiveData<String>()
	val usuario = MutableLiveData<String>()//<List<Boolean>>? = null
	val puestos = MutableLiveData<List<Workstation>>()
	val camino = MutableLiveData<Array<PointF>>()
	val ini = MutableLiveData<PointF>()
	val end = MutableLiveData<PointF>()
	val ini100 = PointF()
	val end100 = PointF()
	val plane = Plane(getApplication())

	//______________________________________________________________________________________________
	init {
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
				Log.e(TAG, "init:userFire.get:e:---------------------------------------------"+auth.getEmail().toString(), error)
			}
		})
	}

	//______________________________________________________________________________________________
	fun logout() { auth.logout() }

	//______________________________________________________________________________________________
	fun punto(pto: PointF, img: CesImgView) {
		val pto100 = img.coordImgTo100(pto)
		//Log.e(TAG, "punto:-----0-----------"+pto100+" :: "+ini.value+" : "+end.value)

		if(pto100.x < 0 || pto100.x >= 100 || pto100.y < 0 || pto100.y >= 100) {
			mensaje.value = getApplication<App>().getString(R.string.error_outside_bounds)
			Log.e(TAG, "punto:e:out of boundaries--------------"+pto+" : "+pto100+" :: "+ini.value+" : "+end.value)
			return
		}
		if(ini.value == null || end.value != null) {
			ini100.set(pto100)
			ini.value = pto
			end.value = null
		}
		else {
			end100.set(pto100)
			end.value = pto
			val sol = plane.calc(ini100, end100)
			camino.value = sol.data
		}
	}

	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = MapaViewModel::class.java.simpleName
	}
}
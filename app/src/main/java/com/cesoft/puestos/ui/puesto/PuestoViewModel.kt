package com.cesoft.puestos.ui.puesto

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.cesoft.puestos.App
import com.cesoft.puestos.R
import com.cesoft.puestos.data.fire.Fire
import com.cesoft.puestos.data.fire.WorkstationFire
import com.cesoft.puestos.models.User
import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.util.Log

/**
 * Created by ccasanova on 18/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class PuestoViewModel(app: Application) : AndroidViewModel(app) {

	private val fire: Fire = getApplication<App>().fire
	val user = getApplication<App>().user
	val wsOwn = getApplication<App>().wsOwn
	val wsUse = getApplication<App>().wsUse

	var puesto: Workstation? = null
	var isDirty: Boolean = false
	val mensaje = MutableLiveData<String>()

	val isUser get() =  user.value?.id == puesto?.idUser
	val isOwner get() = user.value?.id == puesto?.idOwner
	val isAdmin get() = user.value?.type == User.Type.Admin
	val isReserver get() =	isOwner
			|| (wsUse.value == null && user.value?.type == User.Type.Interim)
			|| (wsUse.value == null && user.value?.type == User.Type.Fixed && wsOwn.value?.status == Workstation.Status.Unavailable)


	//______________________________________________________________________________________________
	fun ocupar() {
		if(puesto == null)return
		WorkstationFire.reserve(fire, puesto!!.id, user.value!!.id, { error ->
			if(error == null) {
				mensaje.value = getApplication<App>().getString(R.string.puesto_reserve_ok)
			}
			else {
				Log.e(TAG, "ocupar:e:--------------------------------------", error)
				mensaje.value = getApplication<App>().getString(R.string.puesto_reserve_error)
			}
		})
	}
	//______________________________________________________________________________________________
	fun liberar() {
		if(puesto == null)return
		WorkstationFire.vacate(fire, puesto!!.id, { error ->
			if(error == null) {
				mensaje.value = getApplication<App>().getString(R.string.puesto_vacant_ok)
				Log.e(TAG, "liberar:------------------OK-----------------------"+mensaje.value)

			}
			else {
				Log.e(TAG, "liberar:e:------------------------------------------------------",error)
				mensaje.value = getApplication<App>().getString(R.string.puesto_vacant_error)
			}
		})
	}
	//______________________________________________________________________________________________
	fun guardar(name: String) {
		if(puesto == null || name.isEmpty())return
		WorkstationFire.guardar(fire, puesto!!.id, name, { error ->
			if(error == null) {
				mensaje.value = getApplication<App>().getString(R.string.save_data_ok)
			}
			else {
				Log.e(TAG, "guardar:e:---------------------------------------------------------",error)
				mensaje.value = getApplication<App>().getString(R.string.save_data_error)
			}
		})
	}

	companion object {
		private val TAG: String = PuestoViewModel::class.java.simpleName
	}
}
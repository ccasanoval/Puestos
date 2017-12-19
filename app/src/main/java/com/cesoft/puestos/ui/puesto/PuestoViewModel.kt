package com.cesoft.puestos.ui.puesto

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.cesoft.puestos.App
import com.cesoft.puestos.data.fire.Fire
import com.cesoft.puestos.data.fire.WorkstationFire
import com.cesoft.puestos.models.User
import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.util.Log

/**
 * Created by ccasanova on 18/12/2017
 */
class PuestoViewModel(app: Application) : AndroidViewModel(app) {

	private val fire: Fire = getApplication<App>().fire
	val user: User = getApplication<App>().user!!
	var puesto: Workstation? = null
	var isDirty: Boolean = false

	init {
Log.e("PuestoViewModel", "-----------------------"+user)
	}

	//______________________________________________________________________________________________
	fun ocupar() {
		if(puesto == null)return
		WorkstationFire.ocupar(fire, puesto!!.id, user.id)
	}
}
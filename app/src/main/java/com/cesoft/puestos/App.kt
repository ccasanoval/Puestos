package com.cesoft.puestos

import android.app.Application
import com.cesoft.puestos.data.auth.Auth


//ZOOM ImageView:
//https://blog.fossasia.org/implementing-a-zoomable-imageview-by-extending-the-default-viewpager-in-phimpme-android/

//Arch Comp
//https://developer.android.com/topic/libraries/architecture/adding-components.html

// PATH FINDING
//https://stackoverflow.com/questions/25120786/a-star-algorithm-with-image-map-android
//https://github.com/citiususc/hipster  TOO SLOW AND MEMORY CONSUMING / java.lang.OutOfMemoryError
//https://www.bedroomlan.org/projects/libastar IN C

//INDOOR LOCATION
//https://play.google.com/store/apps/details?id=com.microsoft.msra.followus.app
//https://arxiv.org/ftp/arxiv/papers/1405/1405.5669.pdf & what u did

//TODO: Login: Admin: Permite añadir puestos de trabajo, modificarlos, borrarlos...
//						Permite añadir y editar y borrar usuarios
//						Permite asignar puestos de trabajo
//				User: Permite bucar y ver puestos de trabajo...
//						Permite reservar puestos de trabajo
//TODO: showWorkstations, showFreeWorkstations(day), ...

/**
 * Created by ccasanova on 30/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class App : Application() {
	lateinit var auth: Auth

	override fun onCreate() {
		super.onCreate()
		auth = Auth.getInstance(this)
	}
}
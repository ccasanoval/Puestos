package com.cesoft.puestos

import android.app.Application
import com.cesoft.puestos.data.auth.Auth
import com.cesoft.puestos.data.fire.Fire
import com.squareup.leakcanary.LeakCanary


//ZOOM ImageView:
//https://blog.fossasia.org/implementing-a-zoomable-imageview-by-extending-the-default-viewpager-in-phimpme-android/
//https://github.com/Piasy/BigImageViewer

//Arch Comp
//https://developer.android.com/topic/libraries/architecture/adding-components.html

// PATH FINDING
//https://stackoverflow.com/questions/25120786/a-star-algorithm-with-image-map-android
//https://github.com/citiususc/hipster  TOO SLOW AND MEMORY CONSUMING / java.lang.OutOfMemoryError
//https://www.bedroomlan.org/projects/libastar IN C
//https://github.com/leethomason/MicroPather IN C
//https://github.com/justinhj/astar-algorithm-cpp IN C

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
	lateinit var fire: Fire

	override fun onCreate() {
		super.onCreate()

		/// LEAK CANARY
		if (LeakCanary.isInAnalyzerProcess(this)) {
			// This process is dedicated to LeakCanary for heap analysis.
			// You should not init your app in this process.
			return
		}
		LeakCanary.install(this)

		/// IMG VIEW
		//BigImageViewer.initialize(FrescoImageLoader.with(appContext));
		//BigImageViewer.initialize(GlideImageLoader.with(this))

		/// FIRE AUTH
		auth = Auth.getInstance(this)
		fire = Fire()
	}
}
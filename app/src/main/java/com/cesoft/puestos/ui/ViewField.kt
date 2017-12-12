package com.cesoft.puestos.ui

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View

/**
 * Created by ccasanova on 12/12/2017
 */
object ViewField {
	//______________________________________________________________________________________________
	fun <T : View> Activity.enlaza(@IdRes res: Int): Lazy<T> {
		@Suppress("UNCHECKED_CAST")
		return lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(res) }
	}
}
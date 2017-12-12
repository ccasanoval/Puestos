package com.cesoft.puestos.ui

import android.support.annotation.IdRes
import android.view.View

/**
 * Created by ccasanova on 12/12/2017
 */
object ViewField {
	//______________________________________________________________________________________________
	fun <T : View> BaseActivity.bind(@IdRes res: Int): Lazy<T> {
		@Suppress("UNCHECKED_CAST")
		return lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(res) }
	}
}
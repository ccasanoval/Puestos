package com.cesoft.puestos.util

import com.cesoft.puestos.BuildConfig

/**
 * Created by ccasanova on 08/11/2017
 */
object Log {
	fun e(tag: String, msg: String, e: Throwable? = null) {
		if(BuildConfig.DEBUG)
			android.util.Log.e(tag, msg, e)
	}
	//FirebaseCrash.log("Activity created");
}

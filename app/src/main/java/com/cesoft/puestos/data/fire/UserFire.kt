package com.cesoft.puestos.data.fire

import com.cesoft.puestos.Log
import com.cesoft.puestos.models.User

/**
 * Created by ccasanova on 01/12/2017
 */
class UserFire {

	//______________________________________________________________________________________________
	fun get(fire: Fire, email: String, callback: (User, Throwable?) -> Unit) {
		fire.getRef(ROOT_COLLECTION, email)
			.get()
			.addOnCompleteListener({ task ->
				if(task.isSuccessful) {
					val user = fire.translate(task.result, User::class.java) as User
					callback(user, null)
				}
				else {
					Log.e(TAG, "loadComicList:Firebase:e:------------------------ ", task.exception)
					callback(User(), task.exception)
				}
			})
	}

	//______________________________________________________________________________________________
	companion object {
		val TAG: String = User::class.java.simpleName
		val ROOT_COLLECTION = "usuarios"
	}
}
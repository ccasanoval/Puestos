package com.cesoft.puestos.ui.base

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.cesoft.puestos.models.User
import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.ui.login.LoginActivity
import com.cesoft.puestos.util.Log

/**
 * Created by ccasanova on 30/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
abstract class BaseActivity: AppCompatActivity() {

	private lateinit var viewModel: BaseViewModel

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
		super.onCreate(savedInstanceState, persistentState)
		onCreate(savedInstanceState)
		iniViewModel()
	}
	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		iniViewModel()
	}
	//______________________________________________________________________________________________
	override fun onDestroy() {
		super.onDestroy()
		viewModel.onDestroy()
	}

	//______________________________________________________________________________________________
	private fun toLoginActivity() {
		val intent = Intent(this, LoginActivity::class.java)
		startActivity(intent)
		finish()
	}


	//______________________________________________________________________________________________
	private fun iniViewModel() {
		Log.e(TAG, "iniViewModel:--------------------------------------------------")
		viewModel = ViewModelProviders.of(this).get(BaseViewModel::class.java)
		/*viewModel.user.observe(this, Observer { user -> onUser(user!!) })
		viewModel.wsOwn.observe(this, Observer { pto -> onWorkstationOwn(pto) })
		viewModel.wsUse.observe(this, Observer { pto -> onWorkstationUse(pto) })*/
		viewModel.onCreate({ isNotLogedIn ->
			if(isNotLogedIn)
				toLoginActivity()
		})
	}


	//______________________________________________________________________________________________
	//open
	/*abstract protected fun onUser(user: User)
	//______________________________________________________________________________________________
	abstract protected fun onWorkstationOwn(wsOwn: Workstation?)
	//______________________________________________________________________________________________
	abstract protected fun onWorkstationUse(wsUse: Workstation?)
*/
	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = BaseActivity::class.java.simpleName
	}
}
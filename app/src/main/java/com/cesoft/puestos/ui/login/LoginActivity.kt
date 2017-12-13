package com.cesoft.puestos.ui.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.cesoft.puestos.util.Log
import com.cesoft.puestos.R
import com.cesoft.puestos.App
import com.cesoft.puestos.data.auth.Auth
import com.cesoft.puestos.ui.ViewField.enlaza
import com.cesoft.puestos.ui.mapa.MapaActivity

//TODO: MVVM
/**
 * Created by ccasanova on 30/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class LoginActivity : AppCompatActivity(), View.OnClickListener {

	private val txtStatus: TextView by enlaza(R.id.txtLoginStatus)
	private val txtEmail: EditText by enlaza(R.id.txtLoginEmail)
	private val txtClave: EditText by enlaza(R.id.txtLoginClave)

	private lateinit var auth: Auth

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.act_login)

//		txtStatus = findViewById(R.id.txtLoginStatus)
//		txtEmail = findViewById(R.id.txtLoginEmail)
//		txtClave = findViewById(R.id.txtLoginClave)

		val btnLogin: Button = findViewById(R.id.btnLogin)
		btnLogin.setOnClickListener(this)
		val btnLogout: Button = findViewById(R.id.btnLogout)
		btnLogout.setOnClickListener(this)

		auth = (application as App).auth
	}

	//______________________________________________________________________________________________
	override fun onStart() {
		super.onStart()
		updateUI(auth.getEmail())
	}

	//______________________________________________________________________________________________
	private fun signIn(email: String, password: String) {
		Log.e(TAG, "signIn:----------------------------------------" + email)
		if(!validateForm()) {
			return
		}

		showProgressDialog()

		auth.login(email, password, { auth: Auth, exception: Exception? ->
			if(auth.isLogedIn() && exception == null) {
				Log.e(TAG, "signInWithEmail:success----------------------")
					updateUI(auth.getEmail())
					val intent = Intent(this, MapaActivity::class.java)
					startActivity(intent)
					finish()
			}
			else {
				Log.e(TAG, "signInWithEmail:failure-------------------------", exception)
					Toast.makeText(this@LoginActivity, R.string.error_login, Toast.LENGTH_SHORT).show()
					updateUI(null)
					txtStatus.setText(R.string.error_login)
			}
		})
	}

	//______________________________________________________________________________________________
	private fun signOut() {
		auth.logout()
		updateUI(null)
	}

	//______________________________________________________________________________________________
	private fun validateForm(): Boolean {
		var valid = true
		val email = txtEmail.text.toString()
		if(TextUtils.isEmpty(email)) {
			txtEmail.error = getString(R.string.campo_obligatorio)
			valid = false
		}
		else {
			txtEmail.error = null
		}

		val password = txtClave.text.toString()
		if(TextUtils.isEmpty(password)) {
			txtClave.error = getString(R.string.campo_obligatorio)
			valid = false
		}
		else {
			txtClave.error = null
		}

		return valid
	}

	//______________________________________________________________________________________________
	private fun updateUI(userEmail: String?, isVerifiedEmail: Boolean? = false) {
		hideProgressDialog()
		if(userEmail != null) {
			txtStatus.text = getString(R.string.ok_login, userEmail, isVerifiedEmail)

			var view: View = findViewById(R.id.layLoginBotones)
			view.visibility=(View.GONE)
			view = findViewById(R.id.layLoginFields)
			view.visibility=(View.GONE)
			view = findViewById(R.id.layLogoutBotones)
			view.visibility=(View.VISIBLE)

			finish()
		}
		else {
			txtStatus.setText(R.string.sesion_cerrada)

			var view: View = findViewById(R.id.layLoginBotones)
			view.visibility=(View.VISIBLE)
			view = findViewById(R.id.layLoginFields)
			view.visibility=(View.VISIBLE)
			view = findViewById(R.id.layLogoutBotones)
			view.visibility=(View.GONE)
		}
	}

	//______________________________________________________________________________________________
	override fun onClick(v: View) {
		val i = v.id
		if(i == R.id.btnLogin) {
			signIn(txtEmail.text.toString(), txtClave.text.toString())
		}
		else if(i == R.id.btnLogout) {
			signOut()
		}
	}


	//______________________________________________________________________________________________
	//TODO: cambiar progressDialog
	@VisibleForTesting
	var mProgressDialog: ProgressDialog? = null

	fun showProgressDialog() {
		if(mProgressDialog == null) {
			mProgressDialog = ProgressDialog(this)
			mProgressDialog!!.setMessage(getString(R.string.cargando))
			mProgressDialog!!.isIndeterminate = true
		}

		mProgressDialog!!.show()
	}

	fun hideProgressDialog() {
		if(mProgressDialog != null && mProgressDialog!!.isShowing) {
			mProgressDialog!!.dismiss()
		}
	}

	public override fun onStop() {
		super.onStop()
		hideProgressDialog()
	}

	//______________________________________________________________________________________________
	companion object {
		private val TAG = LoginActivity::class.java.simpleName
	}
}

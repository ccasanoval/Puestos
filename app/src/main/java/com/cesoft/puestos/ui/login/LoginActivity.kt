package com.cesoft.puestos.ui.login

import android.app.ProgressDialog
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.cesoft.puestos.Log
import com.cesoft.puestos.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.cesoft.puestos.App


/**
 * Created by ccasanova on 30/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class LoginActivity : AppCompatActivity(), View.OnClickListener {

	private var txtStatus: TextView? = null
	private var txtEmail: EditText? = null
	private var txtClave: EditText? = null

	private lateinit var fireAuth: FirebaseAuth


	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.act_login)

		txtStatus = findViewById(R.id.txtLoginStatus)
		txtEmail = findViewById(R.id.txtLoginEmail)
		txtClave = findViewById(R.id.txtLoginClave)

		val btnLogin: Button = findViewById(R.id.btnLogin)
		btnLogin.setOnClickListener(this)
		val btnLogout: Button = findViewById(R.id.btnLogout)
		btnLogout.setOnClickListener(this)

		fireAuth = (application as App).fireAuth
	}

	//______________________________________________________________________________________________
	override fun onStart() {
		super.onStart()
		updateUI(fireAuth.currentUser)
	}

	//______________________________________________________________________________________________
	private fun signIn(email: String, password: String) {
		Log.e(TAG, "signIn:----------------------------------------" + email)
		if(!validateForm()) {
			return
		}

		showProgressDialog()

		// [START sign_in_with_email]
		fireAuth.signInWithEmailAndPassword(email, password)
			.addOnCompleteListener(this, { task ->
				if(task.isSuccessful) {
					Log.e(TAG, "signInWithEmail:success----------------------")
					val user = fireAuth.currentUser
					updateUI(user)
				}
				else {
					Log.e(TAG, "signInWithEmail:failure-------------------------", task.exception)
					Toast.makeText(this@LoginActivity, R.string.error_login, Toast.LENGTH_SHORT).show()
					updateUI(null)
					txtStatus!!.setText(R.string.error_login)
				}
				hideProgressDialog()
			})
	}

	//______________________________________________________________________________________________
	private fun signOut() {
		fireAuth.signOut()
		updateUI(null)
	}

	//______________________________________________________________________________________________
	private fun validateForm(): Boolean {
		var valid = true
Log.e(TAG, "validateForm----------------------------------------------")
		val email = txtEmail!!.text.toString()
		if(TextUtils.isEmpty(email)) {
			txtEmail!!.error = getString(R.string.campo_obligatorio)
			valid = false
		}
		else {
			txtEmail!!.error = null
		}

		val password = txtClave!!.text.toString()
		if(TextUtils.isEmpty(password)) {
			txtClave!!.error = getString(R.string.campo_obligatorio)
			valid = false
		}
		else {
			txtClave!!.error = null
		}

		return valid
	}

	//______________________________________________________________________________________________
	private fun updateUI(user: FirebaseUser?) {
		hideProgressDialog()
		if(user != null) {
			txtStatus!!.text = getString(R.string.ok_login, user.email, user.isEmailVerified)

			var view: View = findViewById(R.id.layLoginBotones)
			view.visibility=(View.GONE)
			view = findViewById(R.id.layLoginFields)
			view.visibility=(View.GONE)
			view = findViewById(R.id.layLogoutBotones)
			view.visibility=(View.VISIBLE)

			finish()
		}
		else {
			txtStatus!!.setText(R.string.sesion_cerrada)

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
			Log.e(TAG, "onClick-------------------"+txtEmail!!.text.toString()+" : "+txtClave!!.text.toString())
			signIn(txtEmail!!.text.toString(), txtClave!!.text.toString())
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

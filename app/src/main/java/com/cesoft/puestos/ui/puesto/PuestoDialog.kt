package com.cesoft.puestos.ui.puesto

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.dlg_puesto.*
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.cesoft.puestos.R
import com.cesoft.puestos.data.parcelables.WorkstationParcelable
import com.cesoft.puestos.models.User
import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.ui.dlg.SiNoDialog
import com.cesoft.puestos.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
class PuestoDialog : AppCompatActivity() {
	private lateinit var viewModel : PuestoViewModel

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dlg_puesto)

		iniViewModel()
		iniParcel()
		iniVentana()
		iniBotones()
	}

	//______________________________________________________________________________________________
	private fun iniViewModel() {
		viewModel = ViewModelProviders.of(this).get(PuestoViewModel::class.java)
		viewModel.mensaje.observe(this, Observer { mensaje ->
			Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()
		})
		viewModel.user.observe(this, Observer { user ->
			setBotones()
			setCampos()
		})
		viewModel.wsOwn.observe(this, Observer { puesto ->
			setBotones()
		})
		viewModel.wsOwn.observe(this, Observer { puesto ->
			setBotones()
		})
	}
	//______________________________________________________________________________________________
	private fun iniParcel() {
		val parcel: WorkstationParcelable? = intent.extras.getParcelable(Workstation::class.java.name)
		if(parcel != null)viewModel.puesto = parcel.puesto
	}

	//______________________________________________________________________________________________
	private fun iniVentana(){
		val metrics = DisplayMetrics()
		windowManager.defaultDisplay.getMetrics(metrics)
		val params = window.attributes
		params.x = 0
		params.y = 0
		params.height = metrics.heightPixels-75 //1600
		params.width =  metrics.widthPixels-50//1000
		this.window.attributes = params
	}
	//______________________________________________________________________________________________
	private fun iniBotones() {
		btnGuardar.visibility = if(viewModel.isDirty) View.GONE
								else View.VISIBLE
		btnOcupar.setOnClickListener { ocupar() }
		btnLiberar.setOnClickListener { liberar() }
		btnGuardar.setOnClickListener { guardar() }
		btnCerrar.setOnClickListener { salir() }
	}

	//______________________________________________________________________________________________
	private fun setCampos() {
		if(viewModel.puesto != null) {
			txtId.text = viewModel.puesto!!.id
			txtOwner.text = (viewModel.puesto!!.idOwner)
			txtUser.text = (viewModel.puesto!!.idUser)
			txtName.setText(viewModel.puesto!!.name)
			txtStatus.text = when(viewModel.puesto!!.status) {
				Workstation.Status.Free -> getString(R.string.free)
				Workstation.Status.Occupied -> getString(R.string.occupied)
				Workstation.Status.Unavailable -> getString(R.string.unavailable)
			}
		}
		//txtName.focusable = View.NOT_FOCUSABLE
		txtName.isEnabled = false
		if(viewModel.user.value!!.type == User.Type.Admin
				|| viewModel.user.value!!.id == viewModel.puesto!!.idOwner) {
			txtName.isEnabled = false
		}
	}


	//______________________________________________________________________________________________
	private fun setBotones() {
		btnOcupar.visibility = View.GONE
		btnLiberar.visibility = View.GONE
		btnGuardar.visibility = View.GONE
		if(viewModel.puesto == null)return

		if(viewModel.isAdmin || viewModel.isOwner) {
			btnGuardar.visibility = View.VISIBLE
		}

		when(viewModel.puesto!!.status) {
			Workstation.Status.Free -> {
				if(viewModel.isReserver || viewModel.isOwner || viewModel.isAdmin) {
					btnOcupar.visibility = View.VISIBLE
				}
			}
			Workstation.Status.Occupied -> {
				if(viewModel.isOwner || viewModel.isUser || viewModel.isAdmin) {
					btnLiberar.visibility = View.VISIBLE
				}
			}
			Workstation.Status.Unavailable -> {
				btnLiberar.visibility = View.VISIBLE
			}
		}
	}

	//______________________________________________________________________________________________
	private fun liberar() {
		viewModel.liberar()
	}
	//______________________________________________________________________________________________
	private fun ocupar() {
		viewModel.ocupar()
	}
	//______________________________________________________________________________________________
	private fun guardar() {
		viewModel.guardar(txtName.text.toString())
	}
	//______________________________________________________________________________________________
	private fun salir() {
		if(viewModel.isDirty) {
			SiNoDialog.show(this@PuestoDialog,
					getString(R.string.seguro_salir_cambios),
					{ si -> if(si) finish() })
		}
		else
			finish()
	}

	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = PuestoDialog::class.java.simpleName
	}
}

package com.cesoft.puestos.ui.puesto

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.dlg_puesto.*
import android.util.DisplayMetrics
import android.view.View
import com.cesoft.puestos.R
import com.cesoft.puestos.data.parcelables.WorkstationParcelable
import com.cesoft.puestos.models.User
import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.ui.dlg.SiNoDialog

////////////////////////////////////////////////////////////////////////////////////////////////////
class PuestoDialog : AppCompatActivity() {
	private lateinit var viewModel : PuestoViewModel

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dlg_puesto)

		iniVentana()

		viewModel = ViewModelProviders.of(this).get(PuestoViewModel::class.java)
		/*viewModel.mensaje.observe(this, Observer { mensaje ->
			Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()
		})*/

		val parcel: WorkstationParcelable = intent.extras.getParcelable(Workstation::class.java.name)
		if(parcel != null)viewModel.puesto = parcel.puesto

		iniBotones()

		setBotones()
		setCampos()

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
			txtOwner.setText(viewModel.puesto!!.idOwner)
			txtUser.setText(viewModel.puesto!!.idUser)
			txtName.setText(viewModel.puesto!!.name)
			txtStatus.setText(when(viewModel.puesto!!.status) {
				Workstation.Status.Free -> getString(R.string.free)
				Workstation.Status.Occupied -> getString(R.string.occupied)
				Workstation.Status.Unavailable -> getString(R.string.unavailable)
			})
		}
	}
	//______________________________________________________________________________________________
	private fun setBotones() {
		btnOcupar.visibility = View.GONE
		btnLiberar.visibility = View.GONE
		if(viewModel.puesto == null)return

		when(viewModel.puesto!!.status) {
			Workstation.Status.Free -> {
				// Si es usuario temporal y no tiene ya asignado puesto....
				if(viewModel.user.type == User.Type.Interim) {
					btnOcupar.visibility = View.VISIBLE
				}
			}
			Workstation.Status.Occupied -> {
				// Si es usuario fijo y el puesto esta ocupado por el,
				// o si es usuario temporal y el puesto esta ocupado por el...
				if(viewModel.user.type == User.Type.Fixed ) {
					btnLiberar.visibility = View.VISIBLE
				}
			}
			Workstation.Status.Unavailable -> {
			}
		}
	}

	//______________________________________________________________________________________________
	private fun liberar() {

	}
	//______________________________________________________________________________________________
	private fun ocupar() {
		viewModel.ocupar()
	}
	//______________________________________________________________________________________________
	private fun guardar() {

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
}

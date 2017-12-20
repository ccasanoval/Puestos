package com.cesoft.puestos.ui.mapa

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.PointF
import android.support.v7.widget.Toolbar
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.cesoft.puestos.util.Log
import com.cesoft.puestos.R
import com.cesoft.puestos.data.parcelables.WorkstationParcelable
import com.cesoft.puestos.models.User
import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.ui.BaseActivity
import com.cesoft.puestos.ui.dlg.SiNoDialog
import com.cesoft.puestos.ui.puesto.PuestoDialog
import com.davemorrissey.labs.subscaleview.ImageSource
import kotlinx.android.synthetic.main.act_main.*


/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class MapaActivity : BaseActivity() {

	private lateinit var viewModel : MapaViewModel
	private var imgListener: View.OnTouchListener ?=null

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.act_main)

		val toolbar: Toolbar = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar)
		imgPlano.setImage(ImageSource.asset("plano.jpg"))
		val gesture = getGestureDetector()//Si no uso esta variable, deja de funcionar bien ¿?¿?
		imgListener = View.OnTouchListener { _, motionEvent ->
			gesture.onTouchEvent(motionEvent)
		}
		imgPlano.setOnTouchListener(imgListener)

		iniViewModel()
	}

	//______________________________________________________________________________________________
	override fun onDestroy() {
		super.onDestroy()
		imgPlano.destroyDrawingCache()
		imgPlano.recycle()
	}

	//______________________________________________________________________________________________
	private fun iniViewModel() {
		viewModel = ViewModelProviders.of(this).get(MapaViewModel::class.java)
		viewModel.mensaje.observe(this, Observer { mensaje ->
			Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()
		})
		viewModel.user.observe(this, Observer { usr: User? ->
			Log.e(TAG, "iniViewModel:-----------------------USER-----------------------"+usr)
		})
		viewModel.camino.observe(this, Observer<Array<PointF>> { camino ->
			if(camino == null)	delCamino()
			else				showCamino(camino)
		})
		viewModel.puestos.observe(this, Observer<List<Workstation>> { puestos ->
			when {
				viewModel.modo != MapaViewModel.Modo.Puestos -> // && viewModel.modo != MapaViewModel.Modo.Info ->
					Log.e(TAG, "iniViewModel:puestos:observe:-----------------SIN MODO PUESTOS")
				puestos == null ->
					Toast.makeText(this@MapaActivity, getString(R.string.puestos_get_error), Toast.LENGTH_SHORT).show()
				puestos.isEmpty() ->
					Toast.makeText(this@MapaActivity, getString(R.string.puestos_get_none), Toast.LENGTH_SHORT).show()
				else ->
					showPuestos(puestos)
			}
		})
		viewModel.selected.observe(this, Observer<Workstation> { pto ->
			showSeleccionado(pto)
			if(pto != null) {
				viewModel.selected.value = null
			}
		})
		viewModel.wsOwn.observe(this, Observer<Workstation> { pto ->
			showWSOwn(pto)
		})
		viewModel.ini.observe(this, Observer<PointF>{ pto -> showPointF(true,pto) })
		viewModel.end.observe(this, Observer<PointF>{ pto -> showPointF(false,pto) })
	}

	//______________________________________________________________________________________________
	private fun setTitulo(titulo: String?) {
		if(titulo!=null) {
			val i = titulo.indexOf('@')
			title = if(i > 0) titulo.substring(0, i)
			else titulo
		}
	}

	//// MENU MAIN
	//______________________________________________________________________________________________
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}
	//______________________________________________________________________________________________
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when(item.itemId) {
			R.id.act_ruta -> {
				viewModel.modo = MapaViewModel.Modo.Ruta
				Toast.makeText(this@MapaActivity, getString(R.string.ruta_msg), Toast.LENGTH_SHORT).show()
			}
			R.id.act_info -> {
				viewModel.modo = MapaViewModel.Modo.Info
				Toast.makeText(this@MapaActivity, getString(R.string.info_msg), Toast.LENGTH_SHORT).show()
			}
			R.id.act_lista -> {
				viewModel.modo = MapaViewModel.Modo.Puestos
				Toast.makeText(this@MapaActivity, getString(R.string.puestos_msg), Toast.LENGTH_SHORT).show()
			}
			R.id.act_logout -> {
				SiNoDialog.show(this,
						getString(R.string.seguro_logout),
						{ si -> if(si) viewModel.logout() })
			}
			else ->
				return super.onOptionsItemSelected(item)
		}
		return true
	}

	//______________________________________________________________________________________________
	private fun getGestureDetector() =
		GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
			override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
				if(imgPlano.isReady)
					singleTapConfirmed(e)
				else
					Toast.makeText(this@MapaActivity, getString(R.string.cargando_imagen), Toast.LENGTH_SHORT).show()
				return false
			}
		})

	//______________________________________________________________________________________________
	private fun singleTapConfirmed(me: MotionEvent) {
		val pto = imgPlano.viewToSourceCoord(me.x, me.y)
		val pto100 = imgPlano.coordImgTo100(pto)
		viewModel.punto(pto, pto100)
	}

	//______________________________________________________________________________________________
	private fun showCamino(camino: Array<PointF>) {
		imgPlano.setCamino(camino)
	}
	//______________________________________________________________________________________________
	private fun delCamino() {
		imgPlano.delCamino()
	}
	//______________________________________________________________________________________________
	private fun showPointF(initial:Boolean, pto:PointF?){
		imgPlano.setPoint(initial,pto)
	}
	//______________________________________________________________________________________________
	private fun showPuestos(puestos: List<Workstation>) {
		if(puestos.isNotEmpty())
		imgPlano.setPuestos(puestos)
	}

	//______________________________________________________________________________________________
	private fun showSeleccionado(puesto: Workstation?) {
		imgPlano.setSeleccionado(puesto)
		if(puesto != null) {
			val intent = Intent(this, PuestoDialog::class.java)
			intent.putExtra(Workstation::class.java.name, WorkstationParcelable(puesto))
			startActivity(intent)
		}
	}
	//______________________________________________________________________________________________
	private fun showWSOwn(puesto: Workstation?) {
		imgPlano.setWSOwn(puesto)
	}

	//______________________________________________________________________________________________
	override fun onUser(user: User) {
		//if(user != null)
		/*viewModel.usuario.value = user.name + " : " +
			when(user.type) {
				User.Type.Admin -> getString(R.string.admin)
				User.Type.Fixed -> getString(R.string.fixed)
				User.Type.Interim -> getString(R.string.interim)
			}*/
		setTitulo(user.name + " : " +
			when(user.type) {
				User.Type.Admin -> getString(R.string.admin)
				User.Type.Fixed -> getString(R.string.fixed)
				User.Type.Interim -> getString(R.string.interim)
			})
	}
	//______________________________________________________________________________________________
	override fun onWorkstationOwn(wsOwn: Workstation?) {
		viewModel.wsOwn.value = wsOwn
	}
	//______________________________________________________________________________________________
	override fun onWorkstationUse(wsUse: Workstation?) {
		viewModel.wsOwn.value = wsUse
	}


	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = MapaActivity::class.java.simpleName
	}
}

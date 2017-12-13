package com.cesoft.puestos.ui.mapa

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.PointF
import android.support.v7.widget.Toolbar
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.cesoft.puestos.util.Log
import com.cesoft.puestos.R
import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.ui.BaseActivity
import com.cesoft.puestos.ui.CesImgView
import com.cesoft.puestos.ui.ViewField.enlaza
import com.cesoft.puestos.ui.dlg.Dlg
import com.davemorrissey.labs.subscaleview.ImageSource


/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class MapaActivity : BaseActivity() {

	private lateinit var viewModel : MapaViewModel
	private val imgPlano: CesImgView by enlaza(R.id.imgPlano)
	private var imgListener: View.OnTouchListener ?=null


	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)//BaseActivity@onCreate(savedInstanceState)
		setContentView(R.layout.act_main)

		val toolbar: Toolbar = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar)

		//imgPlano = findViewById(R.id.imgPlano)//https://medium.com/@quiro91/improving-findviewbyid-with-kotlin-4cf2f8f779bb
		//imgPlano.setImage(ImageSource.resource(R.drawable.plano))
		imgPlano.setImage(ImageSource.asset("plano.jpg"))
		//imgPlano.setImage(ImageSource.uri("/sdcard/DCIM/DSCM00123.JPG"));

		//imgPlano.setDoubleTapZoomDuration(200)
		//imgPlano.setDoubleTapZoomScale()
		//imgPlano.setOnTouchListener { _, motionEvent -> getGestureDetector().onTouchEvent(motionEvent) }

		val gesture = getGestureDetector()//Si no uso esta variable, deja de funcionar bien ¿?¿?
		imgListener = View.OnTouchListener { _, motionEvent ->
			gesture.onTouchEvent(motionEvent)
		}
		imgPlano.setOnTouchListener(imgListener)
		//setMinimumDpi
		//registerForContextMenu(imgPlano)

		iniViewModel()
	}
	//______________________________________________________________________________________________
	override fun onDestroy() {
		super.onDestroy()
		imgPlano.destroyDrawingCache()
		imgPlano.recycle()
		//System.gc()
	}

	//______________________________________________________________________________________________
	private fun iniViewModel() {
		viewModel = ViewModelProviders.of(this).get(MapaViewModel::class.java)
		viewModel.mensaje.observe(this, Observer { mensaje ->
			Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()
		})
		viewModel.usuario.observe(this, Observer<String> { usuario ->
			setTituloFromEmail(usuario)
		})
		viewModel.camino.observe(this, Observer<Array<PointF>> { camino ->
			if(camino == null)	delCamino()
			else				showCamino(camino)
		})
		viewModel.puestos.observe(this, Observer<List<Workstation>> { puestos ->
			when {
				viewModel.modo != MapaViewModel.Modo.Puestos ->
					Log.e(TAG, "iniViewModel:puestos:observe:-----------------SIN MODO PUESTOS")
				puestos == null ->
					Toast.makeText(this@MapaActivity, getString(R.string.puestos_get_error), Toast.LENGTH_SHORT).show()
				puestos.isEmpty() ->
					Toast.makeText(this@MapaActivity, getString(R.string.puestos_get_none), Toast.LENGTH_SHORT).show()
				else ->
					showPuestos(puestos)
			}
		})
		viewModel.ini.observe(this, Observer<PointF>{ pto -> drawIni(pto) })
		viewModel.end.observe(this, Observer<PointF>{ pto -> drawEnd(pto) })
	}

	//______________________________________________________________________________________________
	private fun setTituloFromEmail(titulo: String?) {
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
				Log.e(TAG, "onOptionsItemSelected:action_logout:----------------------------------------------")
				Dlg.showSiNo(this,
						getString(R.string.seguro_logout),
						{ si -> if(si) viewModel.logout() })
			}
			else ->
				return super.onOptionsItemSelected(item)
		}
		return true
	}

	//______________________________________________________________________________________________
	/*override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
		super.onRestoreInstanceState(savedInstanceState)
		Log.e(TAG, "onRestoreInstanceState:-------------------------------------------------")
		//if (savedInstanceState?.containsKey(BUNDLE_PAGE) == true)page = savedInstanceState.getInt(BUNDLE_PAGE)
	}
	override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
		super.onRestoreInstanceState(savedInstanceState, persistentState)
		Log.e(TAG, "onRestoreInstanceState:2-------------------------------------------------")
	}
	//______________________________________________________________________________________________
	override fun onSaveInstanceState(outState: Bundle?) {
		super.onSaveInstanceState(outState)
		//outState?.putInt(BUNDLE_PAGE, page)
		Log.e(TAG, "onSaveInstanceState:-------------------------------------------------")

	}*/

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

			override fun onLongPress(e: MotionEvent) {
				if(imgPlano.isReady)
					longPress(e)
				else
					Toast.makeText(this@MapaActivity, getString(R.string.cargando_imagen), Toast.LENGTH_SHORT).show()
			}

//			override fun onDoubleTap(e: MotionEvent): Boolean {
//				if(imgPlano.isReady)
//					doubleTap(e)
//				else
//					Toast.makeText(this@MapaActivity, getString(R.string.cargando_imagen), Toast.LENGTH_SHORT).show()
//				return false
//			}
		})




	//______________________________________________________________________________________________
//	private fun doubleTap(me: MotionEvent) {
//		//val sCoord = imgPlano.viewToSourceCoord(me.x, me.y)
//		//Toast.makeText(this@MapaActivity, "Double tap: " + sCoord.x.toInt() + ", " + sCoord.y.toInt(), Toast.LENGTH_SHORT).show()
//	}
	//______________________________________________________________________________________________
	private fun longPress(me: MotionEvent) {
		val pto = imgPlano.viewToSourceCoord(me.x, me.y)
		val pto100 = imgPlano.coordImgTo100(pto)
		viewModel.punto(pto, pto100)
		//Toast.makeText(this@MapaActivity, "Long press: "+pto.x+", "+pto.y, Toast.LENGTH_SHORT).show()
		//TODO: Show context menu: desde aqui, hasta aqui, infoPuesto, admin:addPuesto, admin:delPuesto ...
	}
	//______________________________________________________________________________________________
	private fun singleTapConfirmed(me: MotionEvent) {
		val pto = imgPlano.viewToSourceCoord(me.x, me.y)
		val pto100 = imgPlano.coordImgTo100(pto)
		viewModel.punto(pto, pto100)
	}


	////////////////// IMG VIEW
	//______________________________________________________________________________________________
	private fun showCamino(camino: Array<PointF>) {
		imgPlano.setCamino(camino)
	}
	private fun delCamino() {
		imgPlano.delCamino()
	}
	//______________________________________________________________________________________________
	private fun drawIni(pto: PointF?) {
		imgPlano.setIni(pto)
	}
	//______________________________________________________________________________________________
	private fun drawEnd(pto: PointF?) {
		imgPlano.setEnd(pto)
	}

	//______________________________________________________________________________________________
	private fun showPuestos(puestos: List<Workstation>) {
//		for(puesto in puestos) {
//			val coord: PointF = imgPlano.coord100ToImg(PointF(puesto.x, puesto.y))
//			imgPlano.show
//		}
		imgPlano.setPuestos(puestos)
	}


	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = MapaActivity::class.java.simpleName
	}
}

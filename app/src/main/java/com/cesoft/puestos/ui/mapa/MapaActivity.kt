package com.cesoft.puestos.ui.mapa

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Point
import android.graphics.PointF
import android.support.v7.widget.Toolbar
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.cesoft.puestos.Log
import com.cesoft.puestos.R
import com.cesoft.puestos.ui.BaseActivity
import com.cesoft.puestos.ui.CesImgView
import com.cesoft.puestos.ui.dlg.Dlg
import com.davemorrissey.labs.subscaleview.ImageSource


/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class MapaActivity : BaseActivity() {

	private lateinit var viewModel : MapaViewModel
	private lateinit var imgPlano: CesImgView
	//private var isReadyMapa = false

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)//BaseActivity@onCreate(savedInstanceState)
		setContentView(R.layout.act_main)

		val toolbar: Toolbar = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar)

		imgPlano = findViewById(R.id.imgPlano)
		imgPlano.setImage(ImageSource.resource(R.drawable.plano))
		//imgPlano.setImage(ImageSource.asset("map.png"))
		//imgPlano.setImage(ImageSource.uri("/sdcard/DCIM/DSCM00123.JPG"));

		//imgPlano.setDoubleTapZoomDuration(200)
		//imgPlano.setDoubleTapZoomScale()
		//imgPlano.setOnTouchListener { _, motionEvent -> getGestureDetector().onTouchEvent(motionEvent) }

		val gesture = getGestureDetector()//Si no uso esta variable, deja de funcionar bien ¿?¿?
		imgPlano.setOnTouchListener { _, motionEvent -> gesture.onTouchEvent(motionEvent) }

		registerForContextMenu(imgPlano)

		///////////// ViewModel Observers
		//
		viewModel = ViewModelProviders.of(this).get(MapaViewModel::class.java)
		viewModel.mensaje.observe(this, Observer {
			mensaje -> Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()
		})
		viewModel.usuario.observe(this, Observer<String> {
			usuario -> setTituloFromEmail(usuario)
		})
		viewModel.camino.observe(this, Observer<Array<PointF>> {
			camino ->
			if(camino == null)
				Toast.makeText(this, getString(R.string.error_camino), Toast.LENGTH_LONG).show()
			else
				drawCamino(camino)
		})
		viewModel.ini.observe(this, Observer<PointF>{
			pto -> drawIni(pto)
		})
		viewModel.end.observe(this, Observer<PointF>{
			pto -> drawEnd(pto)
		})
		////////////
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
			R.id.action_logout -> {
				Dlg.showSiNo(this,
						getString(R.string.seguro_logout),
						{ si -> if(si) viewModel.logout() })
				return true
			}
			else ->
				return super.onOptionsItemSelected(item)
		}
	}
	//// MENU CONTEXT
	//______________________________________________________________________________________________
	override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo)
		Log.e(TAG, "onCreateContextMenu----------------------------------------------------------------")
		menu.setHeaderTitle(getString(R.string.acciones))
		menu.add(0, v.id, 0, getString(R.string.desde_aqui))//groupId, itemId, order, title
		menu.add(0, v.id, 0, getString(R.string.hasta_aqui))
	}
	//______________________________________________________________________________________________
	override fun onContextItemSelected(item: MenuItem): Boolean {
		when {
			item.title == getString(R.string.desde_aqui) -> {
				Toast.makeText(applicationContext, "aaaaaaaaaaa", Toast.LENGTH_LONG).show()
			}
			item.title == getString(R.string.hasta_aqui) -> {
				Toast.makeText(applicationContext, "bbbbbbbbbbb", Toast.LENGTH_LONG).show()
			}
			else -> return false
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

			override fun onDoubleTap(e: MotionEvent): Boolean {
				if(imgPlano.isReady)
					doubleTap(e)
				else
					Toast.makeText(this@MapaActivity, getString(R.string.cargando_imagen), Toast.LENGTH_SHORT).show()
				return false
			}
		})




	//______________________________________________________________________________________________
	private fun doubleTap(me: MotionEvent) {
		//val sCoord = imgPlano.viewToSourceCoord(me.x, me.y)
		//Toast.makeText(this@MapaActivity, "Double tap: " + sCoord.x.toInt() + ", " + sCoord.y.toInt(), Toast.LENGTH_SHORT).show()
	}
	//______________________________________________________________________________________________
	private fun longPress(me: MotionEvent) {
		val pto = imgPlano.viewToSourceCoord(me.x, me.y)
		viewModel.punto(PointF(pto.x, pto.y), imgPlano)
		Toast.makeText(this@MapaActivity, "Long press: "+pto.x+", "+pto.y, Toast.LENGTH_SHORT).show()
		//TODO: Show context menu: desde aqui, hasta aqui, infoPuesto, admin:addPuesto, admin:delPuesto ...
	}
	//______________________________________________________________________________________________
	private fun singleTapConfirmed(me: MotionEvent) {
		//val sCoord = calcCoordenadas(me.x, me.y)
		//Toast.makeText(this@MapaActivity, "Single tap: "+sCoord.x+", "+sCoord.y, Toast.LENGTH_SHORT).show()
	}


	////////////////// IMG VIEW
	//______________________________________________________________________________________________
	private fun drawCamino(camino: Array<PointF>) {
		imgPlano.setCamino(camino)
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
	companion object {
		private val TAG: String = MapaActivity::class.java.simpleName
	}
}

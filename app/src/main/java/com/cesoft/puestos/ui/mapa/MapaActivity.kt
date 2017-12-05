package com.cesoft.puestos.ui.mapa

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.PointF
import android.support.v7.widget.Toolbar
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.cesoft.puestos.Log
import com.cesoft.puestos.R
import com.cesoft.puestos.ui.BaseActivity
import com.cesoft.puestos.ui.dlg.Dlg
import com.cesoft.puestos.util.AStar
import com.cesoft.puestos.util.Astar
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView


/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class MapaActivity : BaseActivity() {

	private lateinit var viewModel : MapaViewModel
	private lateinit var imgPlano: SubsamplingScaleImageView

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)//BaseActivity@onCreate(savedInstanceState)
		setContentView(R.layout.act_main)
		Log.e(TAG, "onCreate----------2------------------------")

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

		/// ViewModel
		viewModel = ViewModelProviders.of(this).get(MapaViewModel::class.java)
		viewModel.usuario.observe(this, Observer<String> {
			usuario -> setTituloFromEmail(usuario)
		})


		val MAP: ByteArray = byteArrayOf(//IntArray = intArrayOf(
		//  0 1 2 3 4 5 6 7 8 910 1 2 3 4 5 6 7 8 920 1 2 3 4 5 6 7 8 930 1 2 3 4 5 6 7 8 940 1 2 3 4 5 6 7 8 950 1 2 3 4 5 6 7 8 960 1 2 3 4 5 6 7
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,//0
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,//1
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,//2
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,1,9,9,9,9,9,9,9,9,9,9,9,1,1,1,//3
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,9,9,9,9,9,9,9,1,1,1,1,1,9,1,1,9,9,1,1,9,1,1,9,1,1,1,//4
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,9,9,9,9,9,9,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,//5
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,1,9,1,1,1,//6
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,1,1,9,1,1,1,1,9,9,9,9,9,9,1,9,1,1,9,9,9,9,1,9,1,1,1,//7
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,9,1,1,1,1,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//8
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,9,9,9,1,1,1,9,9,1,1,1,1,9,1,1,1,1,1,1,1,9,1,9,1,1,1,//9
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,9,1,1,1,1,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//10
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,1,1,9,1,1,1,9,9,1,1,1,1,9,9,1,1,9,9,9,9,9,1,9,1,1,1,//11
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,9,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,1,1,1,//12
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,9,9,9,1,1,1,9,9,1,1,1,1,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//13
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,9,1,1,1,1,9,9,9,1,1,1,1,1,9,1,9,1,1,1,//14
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,9,9,9,1,1,1,9,9,9,9,9,9,9,9,1,1,1,9,9,9,9,1,9,1,1,1,//15
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,9,1,1,1,1,9,9,9,9,1,1,9,1,1,1,1,9,9,1,1,9,1,1,1,//16
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,9,1,1,1,1,9,1,1,9,1,1,9,1,1,1,1,1,1,1,1,9,1,1,1,//17
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,1,1,1,1,1,1,1,9,1,1,9,1,1,9,1,1,1,1,1,1,1,1,9,1,1,1,//18
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,9,9,9,1,1,9,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,1,9,1,1,1,//19
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,9,9,9,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,1,9,1,1,1,//20
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,1,1,1,1,1,1,1,9,9,9,9,9,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//21
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,9,9,9,9,1,1,1,1,9,1,1,1,1,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//22
			1,1,1,1,1,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,1,1,1,1,9,9,1,9,9,1,1,1,9,1,1,1,1,9,9,1,1,9,1,1,1,1,1,9,1,1,1,//23
			1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,9,1,1,1,1,9,1,1,1,1,9,1,1,1,1,1,1,1,9,9,1,9,9,1,1,9,9,9,9,9,9,9,1,1,9,9,9,9,9,1,9,1,1,1,//24
			1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,9,1,1,1,1,1,9,1,1,1,1,1,1,9,9,9,9,9,1,1,1,1,1,1,1,9,9,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,//25
			1,1,1,1,1,9,1,1,1,9,9,1,1,1,1,1,9,1,1,9,9,9,9,1,1,9,9,9,9,9,1,9,1,1,1,9,9,9,9,9,9,1,1,1,1,1,1,9,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,//26
			1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,9,9,9,9,9,9,9,1,1,1,9,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,//27
			1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,9,9,1,1,1,9,9,9,1,1,1,1,1,1,9,9,9,1,1,1,9,9,1,1,9,9,1,1,9,9,9,9,1,9,1,1,1,//28
			1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,9,9,1,1,1,1,1,1,1,1,1,1,1,9,9,9,1,1,1,1,9,9,1,1,9,9,1,1,9,9,9,9,1,9,1,1,1,//29
			1,1,1,1,1,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,1,1,1,1,1,1,1,1,1,9,9,9,9,9,9,1,1,1,1,1,1,1,1,1,9,9,9,9,9,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//30
			1,1,1,1,1,9,1,1,1,9,1,1,9,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,1,1,1,1,1,1,9,9,1,1,1,9,1,1,1,1,1,1,9,9,9,9,1,1,1,1,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//31
			1,1,1,1,1,9,1,1,9,9,1,1,9,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,9,9,1,1,1,9,1,1,1,1,1,1,1,1,1,9,1,1,1,1,9,9,1,1,1,1,9,1,9,1,9,1,1,1,//32
			1,1,1,1,1,1,1,1,1,1,1,1,9,9,9,9,9,9,9,9,9,9,9,9,9,9,1,1,9,1,1,9,1,1,9,9,9,9,9,9,1,1,9,1,1,1,1,9,9,9,9,9,9,9,9,9,1,1,1,9,9,9,9,1,9,1,1,1,//33
			1,1,1,1,1,9,1,1,1,1,1,1,9,1,9,1,1,1,9,1,1,9,1,1,9,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,9,1,1,9,1,1,9,9,9,1,1,1,1,1,1,1,1,9,1,1,1,//34
			1,1,1,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,//35
			1,1,1,1,1,9,1,1,1,9,1,1,9,1,1,9,1,1,9,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,1,9,1,1,1,//36
			1,1,1,1,1,9,9,1,1,9,1,1,9,1,1,9,1,1,9,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,1,1,1,1,1,1,9,9,9,9,1,9,1,1,9,1,1,1,//37
			1,1,1,1,1,9,1,1,1,9,1,1,9,1,1,9,1,1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,9,1,1,1,9,1,1,9,1,1,1,//38
			1,1,1,1,1,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,1,1,1,//39
			1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1//40
		)
		val COLS = 68
		val ROWS = 41

		//val a: Array<java.lang.Integer> = MAP.toTypedArray()
		Log.e("TAG--", "------>"+ AStar().calcMapa(6,21, 8,36,  MAP, COLS,ROWS))
	}


	//______________________________________________________________________________________________
	private fun setTituloFromEmail(titulo: String?) {
		if(titulo!=null) {
			val i = titulo.indexOf('@')
			if(i > 0)
				title = titulo.substring(0, i)
			else
				title = titulo
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
				Toast.makeText(applicationContext, "calling code", Toast.LENGTH_LONG).show()
			}
			item.title == getString(R.string.hasta_aqui) -> {
				Toast.makeText(applicationContext, "sending sms code", Toast.LENGTH_LONG).show()
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
	private fun calcCoordenadas(x: Float, y: Float): PointF {
		val sCoord: PointF = imgPlano.viewToSourceCoord(x, y)
		val x1 = 100*sCoord.x / imgPlano.sWidth
		val y1 = 100*sCoord.y / imgPlano.sHeight
		Log.e(TAG, "------------------"+x+" : "+x1+" : "+imgPlano.sWidth)
		return PointF(x1, y1)
	}

	//______________________________________________________________________________________________
	private fun doubleTap(me: MotionEvent) {
		val sCoord = imgPlano.viewToSourceCoord(me.x, me.y)
		Toast.makeText(this@MapaActivity, "Double tap: " + sCoord.x.toInt() + ", " + sCoord.y.toInt(), Toast.LENGTH_SHORT).show()
	}
	//______________________________________________________________________________________________
	private fun longPress(me: MotionEvent) {
		val sCoord = calcCoordenadas(me.x, me.y)
		Toast.makeText(this@MapaActivity, "Long press: "+sCoord.x+", "+sCoord.y, Toast.LENGTH_SHORT).show()
		//TODO: Show context menu: desde aqui, hasta aqui, infoPuesto, admin:addPuesto, admin:delPuesto ...
		viewModel.punto(sCoord)
	}
	//______________________________________________________________________________________________
	private fun singleTapConfirmed(me: MotionEvent) {
		val sCoord = calcCoordenadas(me.x, me.y)
		Toast.makeText(this@MapaActivity, "Single tap: "+sCoord.x+", "+sCoord.y, Toast.LENGTH_SHORT).show()
	}



	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = MapaActivity::class.java.simpleName
	}
}

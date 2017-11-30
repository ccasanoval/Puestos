package com.cesoft.puestos.ui.mapa

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.PointF
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.os.Bundle
import android.os.PersistableBundle
import android.view.GestureDetector
import android.view.Menu
import android.view.MotionEvent
import android.widget.Toast
import com.cesoft.puestos.Log
import com.cesoft.puestos.R
import com.cesoft.puestos.ui.BaseActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView


//ZOOM ImageView:
//https://blog.fossasia.org/implementing-a-zoomable-imageview-by-extending-the-default-viewpager-in-phimpme-android/

//Arch Comp
//https://developer.android.com/topic/libraries/architecture/adding-components.html

// PATH FINDING
//https://stackoverflow.com/questions/25120786/a-star-algorithm-with-image-map-android
//https://github.com/citiususc/hipster

//INDOOR LOCATION
//https://play.google.com/store/apps/details?id=com.microsoft.msra.followus.app
//https://arxiv.org/ftp/arxiv/papers/1405/1405.5669.pdf & what u did

//TODO: Login: Admin: Permite añadir puestos de trabajo, modificarlos...
//				User: Permite bucar y ver puestos de trabajo...


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

		imgPlano.setOnTouchListener { _, motionEvent -> getGestureDetector().onTouchEvent(motionEvent) }

		/// ViewModel
		viewModel = ViewModelProviders.of(this).get(MapaViewModel::class.java)
		viewModel.usuario.observe(this, Observer<String>
		{
			usuario ->
				title = usuario
		})
	}

	//______________________________________________________________________________________________
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}
	//______________________________________________________________________________________________
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when(item.itemId) {
			R.id.action_logout -> {
				viewModel.logout()
				return true
			}

			else ->
				return super.onOptionsItemSelected(item)
		}
	}

	//______________________________________________________________________________________________
	private fun getGestureDetector() =
		GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
		override fun onSingleTapConfirmed(me: MotionEvent): Boolean {
			if(imgPlano.isReady)
				singleTapConfirmed(me)
			else
				Toast.makeText(this@MapaActivity, getString(R.string.cargando_imagen), Toast.LENGTH_SHORT).show()
			return true
		}

		override fun onLongPress(me: MotionEvent) {
			if(imgPlano.isReady)
				longPress(me)
			else
				Toast.makeText(this@MapaActivity, getString(R.string.cargando_imagen), Toast.LENGTH_SHORT).show()
		}

		override fun onDoubleTap(me: MotionEvent): Boolean {
			if(imgPlano.isReady)
				doubleTap(me)
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

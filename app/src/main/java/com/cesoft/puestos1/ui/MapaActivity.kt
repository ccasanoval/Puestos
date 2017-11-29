package com.cesoft.puestos1.ui

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.os.Bundle
import android.view.GestureDetector
import android.view.Menu
import android.view.MotionEvent
import android.widget.Toast
import com.cesoft.puestos1.R
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView


//ZOOM ImageView:
//https://blog.fossasia.org/implementing-a-zoomable-imageview-by-extending-the-default-viewpager-in-phimpme-android/

//Arch Comp
//https://developer.android.com/topic/libraries/architecture/adding-components.html

// PATH FINDING
//https://stackoverflow.com/questions/25120786/a-star-algorithm-with-image-map-android

/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class MapaActivity : AppCompatActivity() {

	private lateinit var imgPlano: SubsamplingScaleImageView

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.act_main)

		val toolbar: Toolbar = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar)

		imgPlano = findViewById(R.id.imgPlano)
		imgPlano.setImage(ImageSource.resource(R.drawable.plano))
		//imgPlano.setImage(ImageSource.asset("map.png"))
		//imgPlano.setImage(ImageSource.uri("/sdcard/DCIM/DSCM00123.JPG"));

		val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
			override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
				if (imgPlano.isReady) {
					val sCoord = imgPlano.viewToSourceCoord(e.x, e.y)
					val cx = imgPlano.sWidth//imgPlano.width
					val cy = imgPlano.sHeight//height
					Toast.makeText(this@MapaActivity, "Single tap: " + sCoord.x.toInt() + ", " + sCoord.y.toInt()+"  /  "+cx+", "+cy, Toast.LENGTH_SHORT).show()
				}
				else {
					Toast.makeText(this@MapaActivity, getString(R.string.img_still_loading), Toast.LENGTH_SHORT).show()
				}
				return true
			}

			override fun onLongPress(e: MotionEvent) {
				if (imgPlano.isReady) {
					val sCoord = imgPlano.viewToSourceCoord(e.x, e.y)
					val cx = imgPlano.scaleX
					val cy = imgPlano.scaleY//height
					Toast.makeText(this@MapaActivity, "Long press: " + sCoord.x.toInt() + ", " + sCoord.y.toInt()+"  /  "+cx+", "+cy, Toast.LENGTH_SHORT).show()
				} else {
					Toast.makeText(this@MapaActivity, getString(R.string.img_still_loading), Toast.LENGTH_SHORT).show()
				}
			}

			override fun onDoubleTap(e: MotionEvent): Boolean {
				if (imgPlano.isReady) {
					val sCoord = imgPlano.viewToSourceCoord(e.x, e.y)
					Toast.makeText(this@MapaActivity, "Double tap: " + sCoord.x.toInt() + ", " + sCoord.y.toInt(), Toast.LENGTH_SHORT).show()
				} else {
					Toast.makeText(this@MapaActivity, getString(R.string.img_still_loading), Toast.LENGTH_SHORT).show()
				}
				return false
			}
		})
		imgPlano.setOnTouchListener { _, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
	}

	//______________________________________________________________________________________________
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}
	//______________________________________________________________________________________________
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when(item.itemId) {
			R.id.action_zoom -> {
				return true
			}

			R.id.action_ruta ->
				return true

			else ->
				return super.onOptionsItemSelected(item)
		}
	}


	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = MapaActivity::class.java.simpleName
	}
}

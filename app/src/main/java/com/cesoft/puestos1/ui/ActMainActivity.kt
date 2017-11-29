package com.cesoft.puestos1.ui

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.os.Bundle
import android.view.Menu
import com.cesoft.puestos1.Log
import com.cesoft.puestos1.R
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.lang.Exception


//ZOOM ImageView:
//https://blog.fossasia.org/implementing-a-zoomable-imageview-by-extending-the-default-viewpager-in-phimpme-android/

//Arch Comp
//https://developer.android.com/topic/libraries/architecture/adding-components.html

/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class ActMainActivity : AppCompatActivity(), SubsamplingScaleImageView.OnImageEventListener {

	private lateinit var imgPlano: SubsamplingScaleImageView

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.act_main)

		//val toolbar: Toolbar = findViewById(R.id.toolbar)
		//setSupportActionBar(toolbar)

		imgPlano = findViewById(R.id.imgPlano)
		imgPlano.setImage(ImageSource.resource(R.drawable.plano))
		imgPlano.setOnImageEventListener(this)
		//imgPlano.setImage(ImageSource.asset("map.png"))
		//imgPlano.setImage(ImageSource.uri("/sdcard/DCIM/DSCM00123.JPG"));

		/// Workstation Info
		imgPlano.setOnClickListener { v ->
			Log.e(TAG, "imgPlano.setOnClickListener:-------------------------------------------")
			//Toast.makeText(v.context, "Clicked", Toast.LENGTH_SHORT).show()
		}
		/// Route from A to B
		imgPlano.setOnLongClickListener { v ->
			Log.e(TAG, "imgPlano.setOnLongClickListener:---------------------------------------")
			//Toast.makeText(v.context, "Long clicked", Toast.LENGTH_SHORT).show()
			true
		}
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
	//// IMPLEMENTS SubsamplingScaleImageView.OnImageEventListener
	override fun onImageLoaded() { }
	override fun onReady() {	}
	override fun onTileLoadError(p0: Exception?) {	}
	override fun onPreviewReleased() {	}
	override fun onImageLoadError(p0: Exception?) {	}
	override fun onPreviewLoadError(p0: Exception?) {	}

	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = ActMainActivity::class.java.simpleName
	}
}

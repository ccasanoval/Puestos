package com.cesoft.puestos1

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater


//ZOOM ImageView:
//https://blog.fossasia.org/implementing-a-zoomable-imageview-by-extending-the-default-viewpager-in-phimpme-android/

////////////////////////////////////////////////////////////////////////////////////////////////////
class ActMainActivity : AppCompatActivity() {

	private lateinit var imgPlano: ImageView

	//______________________________________________________________________________________________
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.act_main)

		val toolbar: Toolbar = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar)

		imgPlano = findViewById(R.id.imgPlano)
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
				imgPlano.layoutParams.height += 100
				imgPlano.layoutParams.width += 100
				if(imgPlano.layoutParams.width > 2000) {
					imgPlano.layoutParams.height = 1000
					imgPlano.layoutParams.width = 1000
				}
				imgPlano.refreshDrawableState()
				return true
			}

			R.id.action_ruta ->
				//Selecionar un punto de origen y uno puesto destino
				return true

			else ->
				return super.onOptionsItemSelected(item)
		}
	}
}

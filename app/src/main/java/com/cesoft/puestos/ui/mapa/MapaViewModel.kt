package com.cesoft.puestos.ui.mapa

import android.Manifest
import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Build
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import com.cesoft.puestos.App
import com.cesoft.puestos.R
import com.cesoft.puestos.data.auth.Auth
import com.cesoft.puestos.data.fire.Fire
import com.cesoft.puestos.data.fire.WifiFire
import com.cesoft.puestos.data.fire.WorkstationFire
import com.cesoft.puestos.models.User
import com.cesoft.puestos.models.Wifi
import com.cesoft.puestos.models.Workstation
import com.cesoft.puestos.util.*
import java.util.*


/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class MapaViewModel(app: Application) : AndroidViewModel(app) {
	private val auth: Auth = getApplication<App>().auth
	private val fire: Fire = getApplication<App>().fire
	val user: MutableLiveData<User> = getApplication<App>().user
	val wsOwn: MutableLiveData<Workstation> = getApplication<App>().wsOwn
	val wsUse: MutableLiveData<Workstation> = getApplication<App>().wsUse

	val mensaje = MutableLiveData<String>()	//TODO: String to int:IdString
	val puestos = MutableLiveData<List<Workstation>>()
	val selected = MutableLiveData<Workstation>()
	val camino = MutableLiveData<Array<PointF>>()
	val ini = MutableLiveData<PointF>()
	val end = MutableLiveData<PointF>()
	val ini100 = PointF()
	val end100 = PointF()
	val plane = Plane(getApplication())

	//val wifiState = MutableLiveData<Boolean>()
	val wifiPtos = MutableLiveData<List<PointF>>()

	val posicion = MutableLiveData<PointF>()

	//______________________________________________________________________________________________
	private fun getString(id: Int) = getApplication<App>().getString(id)
	private val wifi: WifiTool
		get() = getApplication<App>().wifi

	//______________________________________________________________________________________________
	enum class Modo { Ruta, Info, Puestos, Wifi }
	var modo = Modo.Ruta
		set(value) {
			field = value
			if(modo == Modo.Puestos) {
				getPuestosRT()
			}
			if(modo == Modo.Wifi) {
				getWifiRT()
				wifi.addListener(saveWifis)
			}
			else {
				wifi.delListener(saveWifis)
				wifiPtos.value = null
			}

		}

	//______________________________________________________________________________________________
	init {
		puestos.value = listOf()
	}

	//______________________________________________________________________________________________
	fun onCreate(act: Activity) {
		iniWifi(act)
	}
	//______________________________________________________________________________________________
	fun onDestroy() {
		wifi.unregisterWifiReceiver(getApplication())
	}
	//______________________________________________________________________________________________
	fun logout() {
		auth.logout()
	}

	//______________________________________________________________________________________________
	fun punto(pto: PointF, pto100: PointF) {
		when(modo) {
			Modo.Ruta -> ruta(pto, pto100)
			Modo.Info -> info(pto, pto100)
			Modo.Wifi -> wifi(pto, pto100)
			else -> info(pto, pto100)
		}
	}

	//______________________________________________________________________________________________
	private fun getPuestosRT() {
		WorkstationFire.getAllRT(fire, { lista, error ->
			if(error == null) {
				puestos.value = lista.toList()
			}
			else {
				Log.e(TAG, "getPuestosRT:e:------------------------------------------------------",error)
				mensaje.value = getApplication<App>().getString(R.string.puestos_get_error)
			}
		})
	}
	//______________________________________________________________________________________________
	private fun getPuestosRT(callback: (List<Workstation>) -> Unit = {}) {
		WorkstationFire.getAllRT(fire, { lista, error ->
			if(error == null) {
				puestos.value = lista.toList()
				callback(lista.toList())
			}
			else {
				Log.e(TAG, "getPuestos:e:------------------------------------------------------",error)
				mensaje.value = getApplication<App>().getString(R.string.puestos_get_error)
			}
		})
	}
	//______________________________________________________________________________________________
	private fun getWifiRT() {//callback: (List<PointF>) -> Unit = {}) {
		WifiFire.getAllPosicionRT(fire, { lista, error ->
			if(error == null) {
				wifiPtos.value = lista.toList()
				Log.e(TAG, "getWifiRT:------------------------------------------------------"+lista.size)
				//callback(lista.toList())
			}
			else {
				Log.e(TAG, "getWifiRT:e:------------------------------------------------------",error)
				wifiPtos.value = null
				mensaje.value = getString(R.string.puestos_get_error)
			}
		})
	}

	//______________________________________________________________________________________________
	private fun info(pto: PointF, pto100: PointF) {
		//TODO: Search for nearest workstation: Aun no hay soporte en Firestore para consultas por radio de GeoPoints
		if(puestos.value != null && puestos.value!!.isNotEmpty()) {
			infoHelper(puestos.value!!, pto, pto100)
		}
		else {
			getPuestosRT({ lospuestos -> infoHelper(lospuestos, pto, pto100) })
		}
	}
	//______________________________________________________________________________________________
	private fun infoHelper(puestos: List<Workstation>, pto: PointF, pto100: PointF) {
		//TODO: usar pto + tamaño bits icono...... ¿?
		val MAX = 3f
		val candidatos = puestos.filter { puesto ->
			Math.abs(puesto.x - pto100.x) < MAX && Math.abs(puesto.y - pto100.y) < MAX
		}
		var seleccionado: Workstation? = null
		var minDistancia = 2*MAX
		for(puesto in candidatos) {
			//Log.e(TAG, "BUSCANDO: ------------ info: PTO="+puesto)
			val dis = Math.abs(puesto.x - pto100.x) + Math.abs(puesto.y - pto100.y)
			if(minDistancia > dis) {
				minDistancia = dis
				seleccionado = puesto
				//Log.e(TAG, "ENCONTRADO------------------------- info: PTO="+puesto.name+" : "+puesto)
			}
		}
		selected.value = seleccionado
	}
	//______________________________________________________________________________________________
	private val posWifi = PointF()
	private var isPosWifi = false
	private fun wifi(pto: PointF, pto100: PointF) {
		posWifi.set(pto100)
		isPosWifi = true
		wifi.scan()
	}

	//______________________________________________________________________________________________
	private fun ruta(pto: PointF, pto100: PointF) {
		camino.value = null
		if(pto100.x < 0 || pto100.x >= 100 || pto100.y < 0 || pto100.y >= 100) {
			mensaje.value = getApplication<App>().getString(R.string.error_outside_bounds)
			Log.e(TAG, "punto:e:out of boundaries--------------"+pto+" : "+pto100+" :: "+ini.value+" : "+end.value)
			return
		}
		if(ini.value == null || end.value != null) {
			ini100.set(pto100)
			ini.value = pto
			end.value = null
		}
		else {
			end100.set(pto100)
			end.value = pto

			/// En otro hilo
			Observable.defer({
				Observable.just(plane.calcRuta(ini100, end100))
			})
			.subscribeOn(Schedulers.newThread())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe({it ->
				camino.value = it.data
				if(it.data == null) {
					mensaje.value = getApplication<App>().getString(R.string.error_camino)
				}
				//Log.e(TAG, "punto:calc-ruta: ok="+it.isOk+", pasosBusqueda="+it.pasosBusqueda+", pasos="+it.pasos)
			})
		}
	}


	////////////////////////////////////////////////////////////////////////////////
	//TODO: a clase aparte
	///// WIFI
	//______________________________________________________________________________________________
	fun iniWifi(act: Activity) {
		val app = getApplication<App>()
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
				&& app.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			act.requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), WIFI_RES)
			//After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
			Log.e(TAG, "iniWifi:-------------SIN PERMISO------------------------------")
		}
		else {
			wifi.registerWifiReceiver(app)
		}
	}
	//______________________________________________________________________________________________
	fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		if(requestCode == WIFI_RES && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			wifi.registerWifiReceiver(getApplication())
		}
		else {
			//Toast.makeText(applicationContext, getString(R.string.wifi_permiso_error), Toast.LENGTH_LONG).show()
			mensaje.value = getString(R.string.wifi_permiso_error)
			Log.e(TAG, "onRequestPermissionsResult:-----------------NO SE CONCEDIO PERMISO--------------------------")
		}
	}



	///////////////////////////////////////////////////////////////////////////
	///// INI: LOCATION ----------------------------------------------------------------------------
	var timer = Timer()
	val localizador = fun(wifis: ArrayList<Wifi>) {
		posicion.value = Locator3.locate(wifis)//TODO:--------------------------------------------------------------
	}

	val saveWifis = fun(wifis: ArrayList<Wifi>){
		// Save to Fire
		if(isPosWifi) {
			WifiFire.save(fire, posWifi, wifis, { error ->
				if(error == null) {
					Log.e(TAG, "WIFI: FIRE: onReceive: OK--------------------------------------")
					//TODO: Avisar user mostrando punto: o se carga por RT...
				}
				else {
					Log.e(TAG, "WIFI: FIRE: onReceive:e:--------------------------------------",error)
					//TODO: Avisar user mostrando error
				}
			})
		}
		isPosWifi = false
	}


	//______________________________________________________________________________________________
	fun onResume() {
		//Locator.init(getApplication())
		//Locator2.init(getApplication())
		Locator3.init(getApplication())
		//wifi.registerWifiReceiver(getApplication())
		wifi.addListener(localizador)
		timer = Timer()
		timer.schedule(Task(), 0, 2500)
	}
	//______________________________________________________________________________________________
	fun onPause() {
		//wifi.unregisterWifiReceiver(getApplication())
		wifi.delListener(localizador)
		timer.cancel()
		timer.purge()
	}

	//______________________________________________________________________________________________
	internal inner class Task : TimerTask() {
		override fun run() {
			//Log.e(TAG, "Task:run:------------------------------------"+Date())
			wifi.scan()
		}
	}

	///// END: LOCATION ----------------------------------------------------------------------------


	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = MapaViewModel::class.java.simpleName
		val WIFI_RES: Int = 6969
	}
}
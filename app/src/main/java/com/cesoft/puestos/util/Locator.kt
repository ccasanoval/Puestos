package com.cesoft.puestos.util

import android.app.Application
import android.graphics.PointF
import com.cesoft.puestos.models.Wifi
import org.deeplearning4j.util.ModelSerializer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j


/**
 * Created by ccasanova on 28/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
object Locator {
	private val TAG: String = Locator::class.java.simpleName
	private var net: MultiLayerNetwork ? = null

	val MACS = arrayOf(
			"24:1f:a0:dc:7f:ff",
			"58:35:d9:64:58:80",
			"58:35:d9:64:58:83",
			"58:35:d9:64:58:82",
			"58:35:d9:64:58:87",
			"64:d9:89:99:8d:e3",
			"44:e4:d9:00:76:41",
			"44:e4:d9:00:76:40",
			"64:d9:89:c4:0d:63",
			"44:e4:d9:00:76:47"
			//"e2:55:7d:b3:37:90",
			//"c0:62:6b:8e:96:b0",
			//"64:d9:89:99:8d:e7",
			//"58:35:d9:64:5b:10",
			//"64:d9:89:c4:0d:61",
			//"00:12:5f:0b:bc:70"
	)
	fun getFeatures(wifis: List<Wifi>): DoubleArray {
		return DoubleArray(MACS.size, { i ->
			wifis.firstOrNull { it.bssid == MACS[i] }?.level?.toDouble() ?: -99.0
		})
	}

	//______________________________________________________________________________________________
	fun init(app: Application) {
		if(net != null)return
		try {
			val netRaw = app.assets.open("CesNet.zip")
			net = ModelSerializer.restoreMultiLayerNetwork(netRaw)
		}
		catch(e: Exception) {
			Log.e("Locator", "init: Error cargando la ANN ---------------------------------",e)
		}
	}

	//______________________________________________________________________________________________
	fun locate(wifis: List<Wifi>): PointF? {
		return locate(getFeatures(wifis))
	}
	//______________________________________________________________________________________________
	fun locate(features: DoubleArray): PointF? {
		if(net == null) return null
		//Log.e("Locator", "LOCATE INI ---------------------------------"+java.util.Date().time)

		//Normalizar Entrada
		val maxIn = -25
		val minIn = -99
		val features2 = DoubleArray(features.size, { index ->
			(features[index] - minIn) / (maxIn - minIn)
		})
		val input: INDArray = Nd4j.create(features2)

		//Predecir
		val output: INDArray = net!!.output(input)
		var x = output.getFloat(0)
		var y = output.getFloat(1)

		//Denormalizar Salida
		/*val minOut = 0
		val maxOut = 100
		x *= (maxOut - minOut) + minOut
		y *= (maxOut - minOut) + minOut*/


		//Salida
		//Log.e(TAG, "-----------------output1: "+x+", "+y)
		if(x > 100)x=100f
		if(x < 0)x=0f
		if(y > 100)y=100f
		if(y < 0)y=0f
		//Log.e(TAG, "-----------------output2: "+x+", "+y)
		//Log.e("Locator", "LOCATE END ---------------------------------"+java.util.Date().time)

		return PointF(x,y)
	}

}
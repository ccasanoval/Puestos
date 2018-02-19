package com.cesoft.puestos.util

import android.app.Application
import android.graphics.PointF
import com.cesoft.puestos.models.Wifi
import org.deeplearning4j.util.ModelSerializer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j


/**
 * Created by ccasanova on 16/01/2018
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
object Locator3 {
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
			// Tuve que modificar en el modelo (zip->configuration.json) el valor
			// 'VAR_SCALING_UNIFORM_FAN_AVG' por uno que si tenga DL4J, como:
			//[DISTRIBUTION, ZERO, SIGMOID_UNIFORM, UNIFORM, XAVIER, XAVIER_UNIFORM, XAVIER_FAN_IN, XAVIER_LEGACY, RELU, RELU_UNIFORM]
			val netRaw = app.assets.open("CesNetKeras.zip")//Modelo Keras exportado a Deeplearning4J
			net = ModelSerializer.restoreMultiLayerNetwork(netRaw)
		}
		catch(e: Exception) {
			Log.e(TAG, "init: Error cargando la ANN ---------------------------------",e)
		}
	}

	//______________________________________________________________________________________________
	fun locate(wifis: List<Wifi>): PointF? {
		return locate(getFeatures(wifis))
	}
	//______________________________________________________________________________________________
	fun locate(features: DoubleArray): PointF? {
		if(net == null) return null

		//Normalizar Entrada
		val mean = -65.281481
		val stdDev = 2.145915
		val features2 = DoubleArray(features.size, { index ->
			(features[index] - mean) / stdDev
		})
		val input: INDArray = Nd4j.create(features2)

		//Predecir
		val output: INDArray = net!!.output(input)
		var x = output.getFloat(0)
		var y = output.getFloat(1)

		//Salida
		if(x > 100)x=100f
		if(x < 0)x=0f
		if(y > 100)y=100f
		if(y < 0)y=0f
		//Log.e(TAG, "-----------------output2: "+x+", "+y)
		//Log.e("Locator", "LOCATE END ---------------------------------"+java.util.Date().time)

		return PointF(x,y)
	}

}
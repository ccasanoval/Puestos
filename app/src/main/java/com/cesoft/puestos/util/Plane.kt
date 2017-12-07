package com.cesoft.puestos.util

import android.content.Context
import android.graphics.Point
import android.graphics.PointF
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.cesoft.puestos.Log


/**
 * Created by ccasanova on 04/12/2017
 */
class Plane(context: Context) {
	//val isReady = MutableLiveData<Boolean>()
	var isReady: Boolean = false
		private set
	private val data:  MutableList<Byte> = mutableListOf()
	private var cols = 0
	private var rows = 0

	//data class Solucion(var isSolucion: Boolean, var data: Array<PointF>?)

	//______________________________________________________________________________________________
	init {
		var reader: BufferedReader? = null
		try {
			reader = BufferedReader(InputStreamReader(context.assets.open("mapa.txt")))
			var line: String? = reader.readLine()
			while(line != null) {
				for(c in line) {

					if(c == ',')continue
					data.add((c - '0').toByte())
					if(rows == 0)cols++
				}
				line = reader.readLine()
				rows++
			}
			//Log.e(TAG, "init:---------------SIZE:"+cols+"--"+rows+"--------------------------------------")
			//for(b:Byte in data)Log.e(TAG, ""+b+", ")
			isReady = true
		}
		catch(e: IOException) {
			isReady = false
			Log.e(TAG, "init:e:----------------------------------------------------------------",e)
		}
		finally {
			if(reader != null) {
				try {reader.close()} catch(ignore:Exception){}
			}
		}
	}

	//______________________________________________________________________________________________
	private val isValid: Boolean
	get() {
		Log.e(TAG, "calc0:------------0---"+(cols*rows)+"-------------"+data.size)
		if(!isReady || data.size < 4 || data.size != cols*rows)
			return false
		return true
	}

	//______________________________________________________________________________________________
	fun calc(ini: PointF, end: PointF): Solucion = calc(ini.x, ini.y, end.x, end.y)
	//______________________________________________________________________________________________
	fun calc(iniX: Float, iniY: Float, endX: Float, endY: Float): Solucion {
		Log.e(TAG, "calc0:------------0----------------"+data.size)

		val err = Solucion(false, null)
		if(!isValid) return err

		val mapIniX: Int = (iniX*cols/100).toInt()
		val mapIniY: Int = (iniY*rows/100).toInt()

		val mapEndX = (endX*cols/100).toInt()
		val mapEndY = (endY*rows/100).toInt()

		if(mapIniX >= cols || mapIniX < 0) return err
		if(mapIniY >= rows || mapIniY < 0) return err
		if(mapEndX >= cols || mapEndX < 0) return err
		if(mapEndY >= rows || mapEndY < 0) return err

		Log.e(TAG, "CALC: ------------"+mapIniX+", "+mapIniY+"  /  "+mapEndX+", "+mapEndY)
		val res = Astar().calcMapa(mapIniX, mapIniY, mapEndX, mapEndY, data.toByteArray(), cols, rows)
		Log.e(TAG, res)/////*************************************

		return translateRes(res)
	}

	private fun translateRes(res: String): Solucion {
		//val err = Solucion(false, null)
		//TODO: devolver struct ??
		val gson = Gson()
		val sol = gson.fromJson(res, Solucion::class.java)
		sol.isOk = sol.resultado == "ok"
		if(sol.isOk && sol.camino != null) {
			sol.data = Array(sol.pasos+1, { Point(sol.camino!![it][0], sol.camino!![it][1]) })
			//for(pto: Array<Int> in sol.camino!!)	Log.e(TAG, "---a-----"+pto[0]+","+pto[1])
			//for(pto: Point in sol.data!!)			Log.e(TAG, "---b-----"+pto.x+","+pto.y)
			return sol
		}
		else {
			Log.e(TAG, "err--------------------------------------"+sol.resultado+":\n"+res)
			return Solucion(false, null)
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	/*
		 { "solucion": {
		  	"resultado": "ok",
		  	 "camino": [
			  [ "15", "26" ]
			 , [ "15", "27" ]
		   ..........................
			 , [ "61", "34" ]
			  ], "pasos":"56", "pasosBusqueda": "187" } }
        */
	data class Solucion(
		@Transient var isOk: Boolean,
		@Transient var data: Array<Point>? = null)
	{
		@SerializedName("resultado") var resultado: String ?= null
		@SerializedName("camino") var camino: Array<Array<Int>> ?= null
		@SerializedName("pasos") val pasos: Int = 0
		@SerializedName("pasosBusqueda") val pasosBusqueda: Int = 0
	}


	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = Map::class.java.simpleName
	}
}
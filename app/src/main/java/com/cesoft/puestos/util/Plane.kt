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
	var isReady: Boolean = false
		private set
	private val data:  MutableList<Byte> = mutableListOf()
	private var cols = 0
	private var rows = 0

	//______________________________________________________________________________________________
	init {
		var reader: BufferedReader? = null
		try {
			reader = BufferedReader(InputStreamReader(context.assets.open("mapa.txt")))
			var line: String? = reader.readLine()
			while(line != null) {
				for(c in line) {
					//if(c == ',')continue
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
	fun coordIn(pto: PointF): PointF {
		val x = pto.x *cols/100f
		val y = pto.y *rows/100f
		return PointF(x, y)
	}
	//______________________________________________________________________________________________
	fun coordOut(pto: Point): PointF {
		val x = pto.x *100f/cols
		val y = pto.y *100f/rows
		return PointF(x, y)
	}
	//______________________________________________________________________________________________
	fun calc(ini: PointF, end: PointF): Solucion {
		Log.e(TAG, "calc0:------------0----------------"+data.size)

		val err = Solucion(false, null)
		if(!isValid) return err

		val iniMap = coordIn(ini)
		val endMap = coordIn(end)
		if(iniMap.x >= cols || iniMap.x < 0) return err
		if(iniMap.y >= rows || iniMap.y < 0) return err
		if(endMap.x >= cols || endMap.x < 0) return err
		if(endMap.y >= rows || endMap.y < 0) return err

		Log.e(TAG, "CALC: ------------"+iniMap+" / "+endMap)
		val res = Astar().calcMapa(iniMap, endMap, data.toByteArray(), cols, rows)
		Log.e(TAG, res)/////*************************************

		return translateRes(res)
	}

	//______________________________________________________________________________________________
	private fun translateRes(res: String): Solucion {
		//TODO: devolver struct ??
		val gson = Gson()
		val sol = gson.fromJson(res, Solucion::class.java)
		sol.isOk = sol.resultado == "ok"
		if(sol.isOk && sol.camino != null) {
			sol.data = Array(sol.pasos+1, { coordOut(Point(sol.camino!![it][0], sol.camino!![it][1])) })
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
		@Transient var data: Array<PointF>? = null)
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
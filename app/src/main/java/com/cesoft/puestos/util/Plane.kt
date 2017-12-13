package com.cesoft.puestos.util

import android.content.Context
import android.graphics.Point
import android.graphics.PointF
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


/**
 * Created by ccasanova on 04/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class Plane(context: Context) {
	private var isReady: Boolean = false
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
			Log.e(TAG, "init:---------------SIZE:"+cols+"--"+rows+"--------------------------------------")
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
		Log.e(TAG, "isValid:---------------"+(cols*rows)+" === "+data.size)
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
	fun calcRuta(ini: PointF, end: PointF): Solution {
		Log.e(TAG, "calc0:------------0----------------"+data.size)

		val err = Solution(false, null)
		if(!isValid) return err

		val iniMap = coordIn(ini)
		val endMap = coordIn(end)
		if(iniMap.x >= cols || iniMap.x < 0) return err
		if(iniMap.y >= rows || iniMap.y < 0) return err
		if(endMap.x >= cols || endMap.x < 0) return err
		if(endMap.y >= rows || endMap.y < 0) return err

		iniMap.set(evitarMuros(iniMap))
		endMap.set(evitarMuros(endMap))

		Log.e(TAG, "CALC: ------------"+iniMap+" === "+data[iniMap.y.toInt()*cols+iniMap.x.toInt()]+" / "+endMap +" === "+data[endMap.y.toInt()*cols+endMap.x.toInt()])
		val res = Astar().calcMapa(iniMap, endMap, data.toByteArray(), cols, rows)
		//val sol = translateRes(res)
		//if( ! sol.isOk)
		//Log.e(TAG, res)/////*************************************
		return translateRes(res)
	}
	//______________________________________________________________________________________________
	private fun evitarMuros(ptoIn: PointF): PointF {
		val ptoOut = PointF()
		ptoOut.set(ptoIn)
		val nueve: Byte = 9
		if(data[ptoOut.y.toInt()*cols+ptoOut.x.toInt()] == nueve) {
			for(x in 1..150) {
				for(y in 0..x) {
					if(ptoOut.x -x >= 0 && ptoOut.y -y >= 0
							&& data[(ptoOut.y.toInt() -y) * cols + ptoOut.x.toInt() -x] != nueve) {
						ptoOut.x -= x
						ptoOut.y -= y
						break
					}
					if(ptoOut.x +x < cols && ptoOut.y +y < rows
							&& data[(ptoOut.y.toInt() +y) * cols + ptoOut.x.toInt() +x] != nueve) {
						ptoOut.x += x
						ptoOut.y += y
						break
					}
					if(ptoOut.x -x >= 0 && ptoOut.y +y < rows
							&& data[(ptoOut.y.toInt() +y) * cols + ptoOut.x.toInt() -x] != nueve) {
						ptoOut.x -= x
						ptoOut.y += y
						break
					}
					if(ptoOut.x +x < cols && ptoOut.y -y >= 0
							&& data[(ptoOut.y.toInt() -y) * cols + ptoOut.x.toInt() +x] != nueve) {
						ptoOut.x += x
						ptoOut.y -= y
						break
					}
				}
				if(data[ptoOut.y.toInt()*cols+ptoOut.x.toInt()] != nueve)break
			}
			if(data[ptoOut.y.toInt()*cols+ptoOut.x.toInt()] == nueve)
				Log.e(TAG, "Error: diste con pared ................Avisar a usuario......................."+ptoIn)
		}
		return ptoOut
	}

	//______________________________________________________________________________________________
	private fun translateRes(res: String): Solution {
		//TODO: devolver struct ??
		val gson = Gson()
		val sol = gson.fromJson(res, Solution::class.java)
		sol.isOk = sol.resultado == "ok"
		if(sol.isOk && sol.camino != null) {
			sol.data = Array(sol.pasos+1, { coordOut(Point(sol.camino!![it][0], sol.camino!![it][1])) })
			return sol
		}
		else {
			Log.e(TAG, "err--------------------------------------"+sol.resultado+":\n"+res)
			return sol
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
	data class Solution(
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
		private val TAG: String = Plane::class.java.simpleName
	}
}
package com.cesoft.puestos.util

import android.content.Context
import android.graphics.PointF
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
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

	data class Solucion(var isSolucion: Boolean, var data: Array<PointF>?)

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
		Log.e(TAG, res)

		return translateRes(res)
	}
	//______________________________________________________________________________________________
	//private fun calc(ini: PointF, end: PointF): Boolean = calc(ini.x, ini.y, end.x, end.y)

	private fun translateRes(res: String): Solucion {
		val err = Solucion(false, null)
		/*if(res.indexOf("OK") > 0 && res.indexOf("SOLUTION:") > 0) {
			var r = res
			var i = res.indexOf("(")
			var j: Int = 0
			while(r.isNotEmpty() && i >= 0) {
				//r = r.substring(i+1)
				j = r.indexOf(',', i+1)
				if(j < 0)return err
				val x = r.substring(i+1, j).toInt()
				i = r.indexOf(')', j+1)
				if(j < 0)return err
			}

		}*/
		//TODO: Parsear json
		/*
		 { "solucion": { "resultado":
			  "ok", "camino": [
			  [ "15", "26" ]
			 , [ "15", "27" ]
		   ..........................
			 , [ "60", "34" ]
			 , [ "61", "34" ]
			  ], "pasos":"56", "pasosBusqueda":
			 "187" } }
        */
		return Solucion(false, null)
	}

	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = Map::class.java.simpleName
	}
}
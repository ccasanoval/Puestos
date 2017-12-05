package com.cesoft.puestos.models

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.graphics.PointF
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import com.cesoft.puestos.Log
import com.cesoft.puestos.util.Astar


/**
 * Created by ccasanova on 04/12/2017
 */
class Plane(context: Context) {
	val isReady = MutableLiveData<Boolean>()
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

					if(c == ',')continue
					//else if(c == '1')
					data.add((c - '0').toByte())
					if(rows == 0)cols++
				}
				line = reader.readLine()
				rows++
			}
			Log.e(TAG, "init:---------------SIZE:"+cols+"--"+rows+"--------------------------------------")
			for(b:Byte in data)
				Log.e(TAG, ""+b+", ")
			isReady.value = true
		}
		catch(e: IOException) {
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
		if(data.size < 4 || data.size != cols*rows)
			return false
		return true
	}
	//______________________________________________________________________________________________
	fun calc(iniX: Float, iniY: Float, endX: Float, endY: Float): Boolean {
		Log.e(TAG, "calc0:------------0----------------"+data.size)

		if(!isValid) return false

		val mapIniX: Int = (iniX*cols/100).toInt()
		val mapIniY: Int = (iniY*rows/100).toInt()

		val mapEndX = (endX*cols/100).toInt()
		val mapEndY = (endY*rows/100).toInt()

		if(mapIniX >= cols || mapIniX < 0) return false
		if(mapIniY >= rows || mapIniY < 0) return false
		if(mapEndX >= cols || mapEndX < 0) return false
		if(mapEndY >= rows || mapEndY < 0) return false

		Log.e(TAG, "CALC: ------------"+mapIniX+", "+mapIniY+"  /  "+mapEndX+", "+mapEndY)


		val res = Astar().calcMapa(mapIniX, mapIniY, mapEndX, mapEndY, data.toByteArray(), cols, rows)
		Log.e(TAG, res)

		return true
	}
	//______________________________________________________________________________________________
	private fun calc(ini: PointF, end: PointF): Boolean = calc(ini.x, ini.y, end.x, end.y)


	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = Map::class.java.simpleName
	}
}
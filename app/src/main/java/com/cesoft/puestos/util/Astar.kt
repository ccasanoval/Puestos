package com.cesoft.puestos.util

/**
 * Created by ccasanova on 05/12/2017
 */
class Astar {

	external fun calcMapa(
		iniX: Int, iniY: Int,
		endX: Int, endY: Int,
		mapa: ByteArray,
		cols: Int, rows: Int): String
	companion object {
		init {
			System.loadLibrary("native-lib")
		}
	}
}
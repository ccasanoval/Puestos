package com.cesoft.puestos.util

/**
 * Created by ccasanova on 05/12/2017
 */
class Astar {

	external fun calcMapa(): String
	companion object {
		init {
			System.loadLibrary("native-lib")
		}
	}
}
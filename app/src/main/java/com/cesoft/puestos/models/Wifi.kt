package com.cesoft.puestos.models

/**
 * Created by ccasanova on 26/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
data class Wifi(
	val id: String = "",
	val level: Int = 0,
	val bssid: String = "", //Address
	val ssid: String = "",	//Name
	val date: String = "",
	val x: Float = 0f,
	val y: Float = 0f)
{
	fun createNewWithPosition(x: Float, y: Float) = Wifi(id, level, bssid, ssid, date, x, y)
}

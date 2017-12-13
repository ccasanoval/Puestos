package com.cesoft.puestos.models

/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
data class Status(
	var id: Long = 0L,
	var idWorkstation: Long = 0L,
	var idUserOwner: Long = 0L,
	var idUserUsing: Long = 0L,
	var type: Type = Type.Libre,
	var ini: String = "2017-11-29",
	var end: String = "2017-11-29")
{
	enum class Type(name: String) { Libre("Libre"), Ocupado("Ocupado") }
}

package com.cesoft.puestos.models

/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
//TODO: https://firebase.google.com/docs/firestore/manage-data/structure-data
data class Reservation(
	//var id: Long = 0L,
	var id: String,
	var idWorkstation: String,
	var idUser: String,
	var type: Type = Type.Free,
	var iniDate: String = "2017-11-29",
	var endDate: String = "2017-11-29")
{
	enum class Type(name: String) { Free("Free"), Occupied("Occupied"), Unavailable("Unavailable") }
}

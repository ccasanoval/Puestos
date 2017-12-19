package com.cesoft.puestos.models

/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
data class Workstation(
	val id: String = "",
	val idOwner: String = "",
	val idUser: String = "",
	val name: String = "",
	var status: Status = Status.Unavailable,
	val x: Float = 0f,
	val y: Float = 0f)
{
	enum class Status(name: String) { Free("Free"), Occupied("Occupied"), Unavailable("Unavailable") }
	fun createNewWithPosition(x: Float, y: Float) = Workstation(id, idOwner, idUser, name, status, x, y)
	fun copy(id: String, x: Float, y: Float) = Workstation(id, idOwner, idUser, name, status, x, y)
}

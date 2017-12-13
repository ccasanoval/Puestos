package com.cesoft.puestos.models

/**
 * Created by ccasanova on 29/11/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
data class User(
	//var id: Long = 0L,
	var id: String = "",
	var name: String = "",
	var type: Type = Type.User)
{
	enum class Type(name: String) { Admin("Admin"), User("User") }
}
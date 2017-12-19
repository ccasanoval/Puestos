package com.cesoft.puestos.data.parcelables

import android.os.Parcel
import android.os.Parcelable
import com.cesoft.puestos.models.Workstation

/**
 * Created by ccasanova on 15/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class WorkstationParcelable(var puesto: Workstation) : Parcelable {

	//______________________________________________________________________________________________
	constructor(parcel: Parcel) : this(
		Workstation(
				parcel.readString(),	//id
				parcel.readString(),	//idOwner
				parcel.readString(),	//idUser
				parcel.readString(),	//name
				parcel.readEnum<Workstation.Status>()!!,	//status
				parcel.readFloat(),		//x
				parcel.readFloat()		//y
		)
	)

	//______________________________________________________________________________________________
	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(puesto.id)
		parcel.writeString(puesto.idOwner)
		parcel.writeString(puesto.idUser)
		parcel.writeString(puesto.name)
		parcel.writeEnum(puesto.status)
		parcel.writeFloat(puesto.x)
		parcel.writeFloat(puesto.y)
	}

	//______________________________________________________________________________________________
	override fun describeContents(): Int {
		return 0
	}

	//______________________________________________________________________________________________
	companion object CREATOR : Parcelable.Creator<WorkstationParcelable> {
		inline fun <reified T : Enum<T>> Parcel.readEnum() =
			readInt().let { if(it >= 0) enumValues<T>()[it] else null }
		inline fun <T : Enum<T>> Parcel.writeEnum(value: T?) =
			writeInt(value?.ordinal ?: -1)

		override fun createFromParcel(parcel: Parcel): WorkstationParcelable {
			return WorkstationParcelable(parcel)
		}
		override fun newArray(size: Int): Array<WorkstationParcelable?> {
			return arrayOfNulls(size)
		}
	}
}
package com.cesoft.puestos.ui.dlg

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog


/**
 * Created by ccasanova on 01/12/2017
 */
class Dlg {
	companion object {
		fun showSiNo(context: Context, msj: String, callback: (b: Boolean) -> Unit) {
			val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
				callback(which == DialogInterface.BUTTON_POSITIVE)
				//DialogInterface.BUTTON_NEGATIVE
			}
			val builder = AlertDialog.Builder(context)
			builder.setMessage(msj).setPositiveButton("Sí", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show()
		}
	}
}
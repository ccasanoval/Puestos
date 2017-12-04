package com.cesoft.puestos.ui.mapa

import android.graphics.PointF

/**
 * Created by ccasanova on 04/12/2017
 */
class MapaMarker {
	enum class Type { Ini, End, None }
	var type: Type = Type.None
	var coordenadas = PointF()

}
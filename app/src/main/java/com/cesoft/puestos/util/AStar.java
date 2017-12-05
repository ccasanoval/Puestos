package com.cesoft.puestos.util;

/**
 * Created by ccasanova on 05/12/2017
 */
public class AStar {

	static {
		System.loadLibrary("native-lib");
	}

	public native String calcMapa(
			int iniX, int iniY,
			int endX, int endY,
			byte[] mapa,
			int cols, int rows);
}

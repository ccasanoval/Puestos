package com.cesoft.puestos.models

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

import com.cesoft.puestos.Log
import es.usc.citius.hipster.algorithm.Hipster
import es.usc.citius.hipster.model.function.impl.StateTransitionFunction
import es.usc.citius.hipster.model.problem.ProblemBuilder
import es.usc.citius.hipster.util.examples.maze.Maze2D
import es.usc.citius.hipster.util.Point


/**
 * Created by ccasanova on 04/12/2017
 */
class Plane(context: Context) {
	private val data: MutableList<String> = mutableListOf()

	//______________________________________________________________________________________________
	init {
		var reader: BufferedReader? = null
		try {
			reader = BufferedReader(InputStreamReader(context.assets.open("plane.txt")))
			var line: String? = reader.readLine()
			while(line != null) {
				data.add(line)
				line = reader.readLine()
			}
		}
		catch(e: IOException) {
			Log.e(TAG, "init:e:----------------------------------------------------------------",e)
		}
		finally {
			if(reader != null) {
				try {reader.close()} catch(ignore:Exception){}
			}
		}
	}

	private val isValid: Boolean
	get() {
		if(data.size < 2 || data[0].length < 2)
			return false
		val n = data[0].length
		/*return data.none {
			Log.e(TAG, "--- "+data.size+" --------------- "+n+" -----------"+it.length)
			n != it.length }*/
		return true
	}
	//______________________________________________________________________________________________
	fun calc(iniX: Float, iniY: Float, endX: Float, endY: Float): Boolean {
		Log.e(TAG, "calc0:------------0----------------"+data.size+" : "+ data[0].length)

		if( ! isValid)return false
		val cy = data.size
		val cx = data[0].length

		var mapX: Int = (iniX*cx/100).toInt()
		var mapY: Int = (iniY*cy/100).toInt()
		val ini = Point(mapX, mapY)

		mapX = (endX*cx/100).toInt()
		mapY = (endY*cy/100).toInt()
		val end = Point(mapX, mapY)

		return calc(ini, end)
	}
	//______________________________________________________________________________________________
	private fun calc(ini: Point, end: Point): Boolean {
		try {
			Log.e(TAG, "calc:------------0-----------------------------------------------")

			if(!isValid) return false
			if(ini.x >= data[0].length || ini.x < 0) return false
			if(ini.y >= data.size || ini.y < 0) return false
			if(end.x >= data[0].length || end.x < 0) return false
			if(end.y >= data.size || end.y < 0) return false

			val iniChars = data[ini.y].replaceRange(ini.x, ini.x+1, "S")
			data[ini.y] = iniChars
			val endChars = data[end.y].replaceRange(end.x, end.x+1, "G")
			data[end.y] = endChars
			//data[end.y].toCharArray()[ini.x] = 'G'

			Log.e(TAG, "calc:------------4-----------------------------------------------")
			for(linea in data)Log.e(TAG, linea)
			Log.e(TAG, "calc:------------5-----------------------------------------------")

			val maze = Maze2D(data.toTypedArray())
			Log.e(TAG, "calc:------------6-----------------------------------------------")

			//ini = maze.getInitialLoc()
			//fin = maze.getGoalLoc()
			//SearchProblem
			val problem = ProblemBuilder.create()
				.initialState(ini)
				.defineProblemWithoutActions()
				.useTransitionFunction(object : StateTransitionFunction<Point>() {
					override fun successorsOf(state: Point): Iterable<Point> {
						return maze.validLocationsFrom(state)
					}
				})
				.useCostFunction { transition -> transition.fromState.distance(transition.state) }
				.useHeuristicFunction { state -> state.distance(end) }
				.build()
			Log.e(TAG, "calc:------------8-----------------------------------------------")

			//val res = Hipster.createAStar(problem).search(end) //Out of Memory !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			val res = Hipster.createIDAStar(problem).search(end)//idem
			Log.e(TAG, "calc:------------999----------------------------------------->"+res)
			System.out.println("RES: "+res)
			return true
		}
		catch(e: Exception) {
			Log.e(TAG, "calc:e:----------------------------------------------------------------",e)
			return false
		}
	}


	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = Map::class.java.simpleName
	}
}
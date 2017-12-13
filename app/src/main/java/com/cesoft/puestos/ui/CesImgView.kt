package com.cesoft.puestos.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.cesoft.puestos.util.Log
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.cesoft.puestos.R.drawable
import com.cesoft.puestos.models.Workstation


/**
 * Created by ccasanova on 07/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
class CesImgView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null)
	: SubsamplingScaleImageView(context, attr) {

	private val paint = Paint()

	private val ptoView = PointF()
	private var ptoIni: PointF? = null
	private var ptoEnd: PointF? = null
	private var imgIni: Bitmap? = null
	private var imgEnd: Bitmap? = null

	private var strokeWidth: Int = 0
	private val path = Path()
	//private var sPoints: MutableList<PointF>? = null
	private var caminoOrg: Array<PointF>? = null
	private var camino: Array<PointF>? = null

	private var puestos: List<Workstation>? = null
	private var imgLibre: Bitmap? = null
	private var imgOcupado: Bitmap? = null


	//______________________________________________________________________________________________
	init {
		initialise()
	}
	private fun initialise() {
		//val a = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
		val density = resources.displayMetrics.densityDpi.toFloat()

		/// Desde Hasta
		imgIni = BitmapFactory.decodeResource(this.resources, drawable.ini)
		imgEnd = BitmapFactory.decodeResource(this.resources, drawable.end)
Log.e(TAG, "init:-------------------"+density+" : "+(420f/density))
		var w = density / 420f * imgIni!!.width
		var h = density / 420f * imgIni!!.height
		imgIni = Bitmap.createScaledBitmap(imgIni!!, w.toInt(), h.toInt(), true)
		imgEnd = Bitmap.createScaledBitmap(imgEnd!!, w.toInt(), h.toInt(), true)

		/// Libre Ocupado
		imgLibre = BitmapFactory.decodeResource(this.resources, drawable.libre)
		imgOcupado = BitmapFactory.decodeResource(this.resources, drawable.ocupado)
		w = density / 420f * imgLibre!!.width
		h = density / 420f * imgLibre!!.height
		imgLibre = Bitmap.createScaledBitmap(imgLibre!!, w.toInt(), h.toInt(), true)
		imgOcupado = Bitmap.createScaledBitmap(imgOcupado!!, w.toInt(), h.toInt(), true)

		/// Camino
		strokeWidth = (density / 60f).toInt()
	}
	//______________________________________________________________________________________________
	override fun onReady() {
		super.onReady()
		setCamino(caminoOrg)
	}

	//______________________________________________________________________________________________
	fun setIni(pto: PointF?) {
		ptoIni = pto
		invalidate()
	}
	//______________________________________________________________________________________________
	fun setEnd(pto: PointF?) {
		ptoEnd = pto
		invalidate()
	}
	//______________________________________________________________________________________________
	fun setCamino(valor: Array<PointF>?) {
		caminoOrg = valor
		if(!isReady || valor == null || valor.size < 2)return
		camino = Array(valor.size, {coord100ToImg(valor[it])})
		Log.e(TAG, "setCamino:--------------------a-----"+valor.size+"----"+valor[0]+" : "+camino!![0])
		invalidate()
	}
	//______________________________________________________________________________________________
	fun delCamino() {
		camino = Array(0, { PointF(0f,0f) })
		caminoOrg = camino
		invalidate()
	}
	//______________________________________________________________________________________________
	fun setPuestos(puestos: List<Workstation>) {
		this.puestos = puestos
		for(puesto in puestos) {
			val coord: PointF = coord100ToImg(PointF(puesto.x, puesto.y))
			puesto.x = coord.x
			puesto.y = coord.y
		}
		invalidate()
	}

	//______________________________________________________________________________________________
	private fun coord100ToImg(pto: PointF): PointF {
		if( ! isReady)return PointF(0f,0f)
		//Log.e(TAG, "coord100ToImg:-----------"+isReady+"------"+sWidth+" / "+sHeight+" ::: "+pto.x+" :: "+imgIni!!.width)
		val x = pto.x *sWidth/100f
		val y = pto.y * sHeight/100f
		return PointF(x, y)
	}
	//______________________________________________________________________________________________
	fun coordImgTo100(pto: PointF): PointF {
		val x = pto.x *100f/sWidth
		val y = pto.y *100f/sHeight
		return PointF(x, y)
	}

	//______________________________________________________________________________________________
	private var preView = PointF()
	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)

		if(!isReady) return

		paint.isAntiAlias = true

		/// PTO INICIO
		drawIni(canvas)
		/// PTO DESTINO
		drawEnd(canvas)
		/// CAMINO
		drawCamino(canvas)
		/// PUESTOS
		drawPuestos(canvas)
	}
	//______________________________________________________________________________________________
	private fun drawIni(canvas: Canvas) {
		if(ptoIni != null) {
			sourceToViewCoord(ptoIni!!, ptoView)
			//Log.e(TAG, "onDraw:ini:---------------------------src:"+ptoIni+" : view:"+ptoView)
			val x = ptoView.x - imgIni!!.width / 2
			val y = ptoView.y - imgIni!!.height
			canvas.drawBitmap(imgIni!!, x, y, paint)
		}
	}
	//______________________________________________________________________________________________
	private fun drawEnd(canvas: Canvas) {
		if(ptoEnd != null) {
			sourceToViewCoord(ptoEnd!!, ptoView)
			//Log.e(TAG, "onDraw:end:---------------------------src:"+ptoEnd+" : view:"+ptoView)
			val x = ptoView.x - imgEnd!!.width / 2
			val y = ptoView.y - imgEnd!!.height
			canvas.drawBitmap(imgEnd!!, x, y, paint)
		}
	}
	//______________________________________________________________________________________________
	private fun drawCamino(canvas: Canvas) {
		path.reset()
		if(camino != null && camino!!.size >= 2) {
			path.reset()
			sourceToViewCoord(camino!![0].x, camino!![0].y, preView)
			path.moveTo(preView.x, preView.y)
			//Log.e(TAG, "onDraw:camino:-----------------------src:"+camino!![0]+" : view:"+preView)
			for(i in 1 until camino!!.size) {
				sourceToViewCoord(camino!![i].x, camino!![i].y, ptoView)
				//Log.e(TAG, "camino:-----------------------src:"+camino!![i]+" : view:"+ptoView)
				path.quadTo(preView.x, preView.y, (ptoView.x + preView.x) / 2, (ptoView.y + preView.y) / 2)
				preView = ptoView
			}
			paint.style = Paint.Style.STROKE
			paint.strokeCap = Paint.Cap.ROUND
			paint.strokeWidth = (strokeWidth * 2).toFloat()
			paint.color = Color.BLACK
			canvas.drawPath(path, paint)
			paint.strokeWidth = strokeWidth.toFloat()
			paint.color = Color.argb(255, 38, 166, 154)
			canvas.drawPath(path, paint)
		}
	}
	//______________________________________________________________________________________________
	private fun drawPuestos(canvas: Canvas) {
		if(puestos != null) {
			for(puesto in puestos!!) {
				sourceToViewCoord(PointF(puesto.x, puesto.y), ptoView)
				val x = ptoView.x - imgLibre!!.width / 2
				val y = ptoView.y - imgLibre!!.height /2
				//TODO: esta libre u ocupado ????
				canvas.drawBitmap(imgLibre!!, x, y, paint)
			}
		}
	}

	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = CesImgView::class.java.simpleName
	}
}

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
	private var caminoOrg: Array<PointF>? = null
	private var camino: Array<PointF>? = null

	private var puestos: List<Workstation>? = null
	private var imgFree: Bitmap? = null
	private var imgOccupied: Bitmap? = null
	private var imgUnavailable: Bitmap? = null
	private var seleccionado: Workstation? = null
	private var imgSelected: Bitmap? = null

	//______________________________________________________________________________________________
	init {
		initialise()
	}
	private fun initialise() {
		val density = resources.displayMetrics.densityDpi.toFloat()

		/// Desde Hasta
		imgIni = BitmapFactory.decodeResource(this.resources, drawable.rute_ini)
		imgEnd = BitmapFactory.decodeResource(this.resources, drawable.rute_end)
		Log.e(TAG, "init:-------------------"+density+" : "+(420f/density))
		var w = density / 420f * imgIni!!.width
		var h = density / 420f * imgIni!!.height
		imgIni = Bitmap.createScaledBitmap(imgIni!!, w.toInt(), h.toInt(), true)
		imgEnd = Bitmap.createScaledBitmap(imgEnd!!, w.toInt(), h.toInt(), true)

		/// Libre Ocupado
		imgFree = BitmapFactory.decodeResource(this.resources, drawable.pto_free)
		imgOccupied = BitmapFactory.decodeResource(this.resources, drawable.pto_occupied)
		imgUnavailable = BitmapFactory.decodeResource(this.resources, drawable.pto_unavailable)
		imgSelected = BitmapFactory.decodeResource(this.resources, drawable.pto_selected)
		w = density / 420f * imgFree!!.width
		h = density / 420f * imgFree!!.height
		imgFree = Bitmap.createScaledBitmap(imgFree!!, w.toInt(), h.toInt(), true)
		imgOccupied = Bitmap.createScaledBitmap(imgOccupied!!, w.toInt(), h.toInt(), true)
		imgUnavailable = Bitmap.createScaledBitmap(imgUnavailable!!, w.toInt(), h.toInt(), true)
		imgSelected = Bitmap.createScaledBitmap(imgSelected!!, w.toInt(), h.toInt(), true)

		/// Camino
		strokeWidth = (density / 60f).toInt()
	}

	//______________________________________________________________________________________________
	override fun onReady() {
		super.onReady()
		setCamino(caminoOrg)
	}
	//______________________________________________________________________________________________
	fun setPoint(init:Boolean, pto: PointF?){
		if (init) ptoIni = pto
		else {ptoEnd = pto}
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
		this.puestos = List(puestos.size, { it ->
			val coord: PointF = coord100ToImg(PointF(puestos[it].x, puestos[it].y))
			puestos[it].createNewWithPosition(coord.x, coord.y)
		})
		invalidate()
	}
	//______________________________________________________________________________________________
	fun setSeleccionado(puesto: Workstation?) {
		if(puesto != null) {
			val coord: PointF = coord100ToImg(PointF(puesto.x, puesto.y))
			seleccionado = puesto.createNewWithPosition(coord.x, coord.y)
		}
		invalidate()
	}

	//______________________________________________________________________________________________
	private fun coord100ToImg(pto: PointF): PointF {
		if( ! isReady)return PointF(0f,0f)
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
		draw(true,canvas)
		/// PTO DESTINO
		draw(false,canvas)
		/// CAMINO
		drawCamino(canvas)
		/// PUESTOS
		drawPuestos(canvas)
		/// SELECCIONADO
		drawSeleccionado(canvas)
	}
	//______________________________________________________________________________________________
	private fun draw(isInitial: Boolean, canvas: Canvas){
		val pto: PointF
		val img: Bitmap
		if(isInitial){
			if(ptoIni == null || imgIni == null){
				//Log.e(TAG,"ERROR:INI: pto o img null: "+ptoIni+", "+imgIni)
				return
			}
			pto = ptoIni!!
			img = imgIni!!
		}else {
			if(ptoEnd == null || imgEnd == null){
				//Log.e(TAG,"ERROR:END: pto o img null: "+ptoEnd+", "+imgEnd)
				return
			}
			pto = ptoEnd!!
			img = imgEnd!!
		}
		drawBitmap(pto, img, canvas)
	}

	//______________________________________________________________________________________________
	private fun drawBitmap(pto: PointF, img: Bitmap, canvas: Canvas) {
		sourceToViewCoord(pto, ptoView)
		val x = ptoView.x - img.width / 2
		val y = ptoView.y - img.height
		canvas.drawBitmap(img, x, y, paint)
	}

	//______________________________________________________________________________________________
	private fun drawCamino(canvas: Canvas) {
		path.reset()
		if(camino != null && camino!!.size >= 2) {
			path.reset()
			sourceToViewCoord(camino!![0].x, camino!![0].y, preView)
			path.moveTo(preView.x, preView.y)
			for(i in 1 until camino!!.size) {
				sourceToViewCoord(camino!![i].x, camino!![i].y, ptoView)
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
				val img = when(puesto.status) {
					Workstation.Status.Free -> imgFree!!
					Workstation.Status.Occupied -> imgOccupied!!
					Workstation.Status.Unavailable -> imgUnavailable!!
				}
				val x = ptoView.x - img.width / 2
				val y = ptoView.y - img.height /2
				canvas.drawBitmap(img, x, y, paint)
			}
		}
	}
	//______________________________________________________________________________________________
	fun drawSeleccionado(canvas: Canvas) {
		if(seleccionado != null) {
			sourceToViewCoord(PointF(seleccionado!!.x, seleccionado!!.y), ptoView)
			val img = imgSelected!!
			val x = ptoView.x - img.width / 2
			val y = ptoView.y - img.height /2
			canvas.drawBitmap(img, x, y, paint)
		}
	}

	//______________________________________________________________________________________________
	companion object {
		private val TAG: String = CesImgView::class.java.simpleName
	}
}

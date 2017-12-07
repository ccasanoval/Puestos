package com.cesoft.puestos.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.cesoft.puestos.Log
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.cesoft.puestos.R.drawable


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


	//______________________________________________________________________________________________
	init {
		initialise()
	}
	private fun initialise() {
		//val a = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
		val density = resources.displayMetrics.densityDpi.toFloat()
		imgIni = BitmapFactory.decodeResource(this.resources, drawable.ini)
		imgEnd = BitmapFactory.decodeResource(this.resources, drawable.end)
//Log.e(TAG, "init:-------------------"+density+" : "+(420f/density))
		val w = density / 420f * imgIni!!.width
		val h = density / 420f * imgIni!!.height
		imgIni = Bitmap.createScaledBitmap(imgIni!!, w.toInt(), h.toInt(), true)
		imgEnd = Bitmap.createScaledBitmap(imgEnd!!, w.toInt(), h.toInt(), true)

		strokeWidth = (density / 60f).toInt()
	}
	//______________________________________________________________________________________________
	override fun onReady() {
		super.onReady()
		setCamino(caminoOrg)
	}

	//______________________________________________________________________________________________
	fun setIni(pto: PointF?) {
		this.ptoIni = pto
		//initialise()
		invalidate()
	}
	//______________________________________________________________________________________________
	fun setEnd(pto: PointF?) {
		this.ptoEnd = pto
		//initialise()
		invalidate()
	}
	//______________________________________________________________________________________________
	fun setCamino(valor: Array<PointF>?) {
		caminoOrg = valor
		if(!isReady || valor == null || valor.size < 2)return
		camino = Array(valor.size, {coord100ToImg(valor[it])})
		Log.e(TAG, "setCamino:--------------------a-----"+valor.size+"----"+valor[0]+" : "+camino!![0])
		//initialise()
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

		if(ptoIni != null) {
			sourceToViewCoord(ptoIni!!, ptoView)
			Log.e(TAG, "onDraw:ini:---------------------------src:"+ptoIni+" : view:"+ptoView)
			val vX = ptoView.x - imgIni!!.width / 2
			val vY = ptoView.y - imgIni!!.height
			canvas.drawBitmap(imgIni!!, vX, vY, paint)
		}

		if(ptoEnd != null) {
			sourceToViewCoord(ptoEnd!!, ptoView)
			Log.e(TAG, "onDraw:end:---------------------------src:"+ptoEnd+" : view:"+ptoView)
			val vX = ptoView.x - imgEnd!!.width / 2
			val vY = ptoView.y - imgEnd!!.height
			canvas.drawBitmap(imgEnd!!, vX, vY, paint)
		}

		if(camino != null && camino!!.size >= 2) {
			path.reset()
			sourceToViewCoord(camino!![0].x, camino!![0].y, preView)
			path.moveTo(preView.x, preView.y)
			Log.e(TAG, "onDraw:camino:-----------------------src:"+camino!![0]+" : view:"+preView)
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
	companion object {
		private val TAG: String = CesImgView::class.java.simpleName
	}
}

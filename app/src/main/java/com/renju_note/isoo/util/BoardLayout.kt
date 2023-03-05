package com.renju_note.isoo.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.renju_note.isoo.data.BoardSetting

class BoardLayout(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private var lineInterval : Float = 0f
    private var density : Float = 0f
    private val paint = Paint()
    private val bounds = Rect()

    private val stones = HashMap<String, StoneView>()
    private var boardSetting = BoardSetting.getDefaultSetting()

    interface Element {
        fun draw(canvas : Canvas?)
    }

    inner class Line(private val startX : Float, private val startY : Float, private val endX : Float, private val endY : Float) {
        fun draw(canvas : Canvas?) {
            paint.apply {
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 7f
                pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            }
            canvas?.drawLine(startX, startY, endX, endY, paint)
        }
    }

    inner class Square(private var left : Float, private var top : Float, private var right : Float, private var bottom : Float) {
        fun draw(canvas : Canvas?) {
            paint.apply {
                color = Color.parseColor("#2B000000")
                style = Paint.Style.FILL
            }
            val tmp = lineInterval / 2
            val rect = RectF(left - tmp, top - tmp, right + tmp, bottom + tmp)
            val path = Path()
            path.addRoundRect(rect, 40f, 40f, Path.Direction.CW)
            canvas?.drawPath(path, paint)
        }
    }

    inner class Point(private val x : Float, private val y : Float) {
        fun draw(canvas : Canvas?) {
            paint.apply {
                color = Color.RED
            }
            canvas?.drawCircle(x, y, 10f, paint)
        }
    }

    enum class StoneViewType { BLACK, WHITE }

    inner class StoneView(private var type : StoneViewType, private var index : Int, private val x : Int, private val y : Int) : Element {

        override fun draw(canvas: Canvas?) {
            TODO("Not yet implemented")
        }

    }

    open class OnBoardTouchListener {
        open fun getCoordinates(x : Int, y : Int) {
            return
        }
    }

    private var onBoardTouchListener = OnBoardTouchListener()

    fun setOnBoardTouchListener(listener : OnBoardTouchListener) {
        onBoardTouchListener = listener
    }

    init {
        setBackgroundColor(Color.TRANSPARENT)
        density = context.resources.displayMetrics.density
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateBoardStatus(boardSetting)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        lineInterval = MeasureSpec.getSize(widthMeasureSpec).toFloat() / 16
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.isAntiAlias = true
        // board background
        paint.color = Color.parseColor(boardSetting.boardColor)
        canvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // board lines
        paint.color = Color.parseColor(blendColors("#FFFFFF", boardSetting.lineColor))
        paint.strokeWidth = (width / 351.3 + 0.5).toInt().toFloat()
        val x = lineInterval
        for (i in 1..15) {
            canvas.drawLine(x * i + x / 2, x - x / 2, x * i + x / 2, x * 15 - x / 2, paint)
            canvas.drawLine(x + x / 2, x * i - x / 2, x * 15 + x / 2, x * i - x / 2, paint)
        }
        canvas.drawCircle(x * 8 + x / 2, x * 8 - x / 2, (width / 150.5 + 0.5).toInt().toFloat(), paint)
        canvas.drawCircle(x * 4 + x / 2, x * 12 - x / 2, (width / 150.5 + 0.5).toInt().toFloat(), paint)
        canvas.drawCircle(x * 12 + x / 2, x * 12 - x / 2, (width / 150.5 + 0.5).toInt().toFloat(), paint)
        canvas.drawCircle(x * 4 + x / 2, x * 4 - x / 2, (width / 150.5 + 0.5).toInt().toFloat(), paint)
        canvas.drawCircle(x * 12 + x / 2, x * 4 - x / 2, (width / 150.5 + 0.5).toInt().toFloat(), paint)

        canvas.drawLine(x / 2, x * 15, x / 2, x * 16, paint)
        canvas.drawLine(0f, x * 15 + x / 2, x, x * 15 + x / 2, paint)

        // board coordinate
        paint.textSize = lineInterval / 2.2f
        paint.typeface = Typeface.MONOSPACE
        for(i in 0..14) {
            val mText = (15 - i).toString()
            paint.getTextBounds("45", 0, mText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            canvas.drawText(mText, x / 2 - textWidth / 2, x * i + x / 2 + textHeight / 2, paint)
        }
        for(i in 0..14) {
            val mText = (i + 65).toChar().toString()
            paint.getTextBounds("A", 0, mText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            canvas.drawText(mText, x * (i + 1) + x / 2 - textWidth / 2, x * 15 + x / 2 + textHeight / 2, paint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x: Float
        val y: Float
        try {
            if (event.action == MotionEvent.ACTION_UP) {
                x = if ((event.x + lineInterval / 2) % lineInterval <= lineInterval / 2) event.x - (event.x + lineInterval / 2) % lineInterval else event.x - (event.x + lineInterval / 2) % lineInterval + lineInterval
                y = if ((event.y - lineInterval / 2) % lineInterval <= lineInterval / 2) event.y - (event.y - lineInterval / 2) % lineInterval else event.y - (event.y - lineInterval / 2) % lineInterval + lineInterval
                if (((x - lineInterval / 2) / lineInterval + 0.5).toInt() - 1 >= 0 && ((y + lineInterval / 2) / lineInterval + 0.5).toInt() - 1 < 15) { // if x,y is in board
                    val xc = ((x - lineInterval / 2) / lineInterval + 0.5).toInt() - 1
                    val yc = ((y + lineInterval / 2) / lineInterval + 0.5).toInt() - 1
                    onBoardTouchListener.getCoordinates(xc, yc)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    fun placeStones(addStones : ArrayList<Pair<String, StoneView>>?, deleteStonesID : ArrayList<String>?) {
        if (deleteStonesID != null) {
            for (deleteStoneID in deleteStonesID) {
                val removeStone = stones.remove(deleteStoneID)
                if(removeStone != null)
                    removeViewInLayout(findViewById(removeStone.viewID))
            }
        }

        if (addStones != null) {
            for(addStone in addStones) {
                if(stones.contains(addStone.first)) {
                    stones[addStone.first]!!.setStoneText(addStone.second.getText())
                    stones[addStone.first]!!.setStoneType(addStone.second.getStoneType())
                } else {
                    stones[addStone.first] = addStone.second
                    addStone.second.addStone(this)
                }
            }
        }
    }

    fun removeAllStones() {
        stones.keys.forEach { key ->
            removeViewInLayout(findViewById(stones[key]!!.viewID))
        }
        stones.clear()
    }

    fun generateStoneID(x : Int, y : Int) : String = "$x/$y"

    fun getRealX(x : Int) : Float = (x + 1) * lineInterval + lineInterval / 2

    fun getRealY(y : Int) : Float = (y + 1) * lineInterval - lineInterval / 2

    fun updateBoardStatus(boardSetting : BoardSetting) {
        this.boardSetting = boardSetting
        stones.keys.forEach { key ->
            stones[key]!!.updateStoneView()
        }
        this.invalidate()
    }

    private fun blendColors(baseColor: String, blendColor: String): String {
        val base = Color.parseColor(baseColor)
        val blend = Color.parseColor(blendColor)
        val alpha = Color.alpha(blend) / 255f
        val red = (1 - alpha) * Color.red(base) + alpha * Color.red(blend)
        val green = (1 - alpha) * Color.green(base) + alpha * Color.green(blend)
        val blue = (1 - alpha) * Color.blue(base) + alpha * Color.blue(blend)
        val alphaInt = (alpha * 255).toInt()
        val colorInt = Color.argb(alphaInt, red.toInt(), green.toInt(), blue.toInt())
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }

}
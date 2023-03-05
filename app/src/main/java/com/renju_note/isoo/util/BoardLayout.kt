package com.renju_note.isoo.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.renju_note.isoo.data.BoardSetting
import com.renju_note.isoo.data.SequenceSetting

class BoardLayout(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private var lineInterval : Float = 0f
    private var density : Float = 0f
    private val paint = Paint()
    private val bounds = Rect()

    private var boardSetting = BoardSetting.getDefaultSetting()
    private var sequenceSetting = SequenceSetting.getDefaultSetting()

    private val stones = HashMap<String, StoneView>()
    private val childs = ArrayList<Child>()
    private val drawingElements = ArrayList<Element>() // Line, Area
    private val points = ArrayList<Point>()

    interface Element {
        fun draw(canvas : Canvas?)
    }

    inner class Line(private val startX : Int, private val startY : Int, private val endX : Int, private val endY : Int) : Element {
        override fun draw(canvas : Canvas?) {
            paint.apply {
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = lineInterval / 9.4107f
                pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            }
            canvas?.drawLine(getRealX(startX), getRealY(startY), getRealX(endX), getRealY(endY), paint)
        }
    }

    inner class Area(private var left : Int, private var top : Int, private var right : Int, private var bottom : Int) : Element {
        override fun draw(canvas : Canvas?) {
            paint.apply {
                color = Color.parseColor("#2B000000")
                style = Paint.Style.FILL
            }
            if(left > right) {
                val tmp = left
                left = right
                right = tmp
            }
            if(top > bottom) {
                val tmp = top
                top = bottom
                bottom = tmp
            }
            val margin = lineInterval / 2
            val rect = RectF(left - margin, top - margin, right + margin, bottom + margin)
            val path = Path()
            path.addRoundRect(rect, lineInterval / 1.6468f, lineInterval / 1.6468f, Path.Direction.CW)
            canvas?.drawPath(path, paint)
        }
    }

    inner class Point(private val x : Int, private val y : Int) : Element {
        override fun draw(canvas : Canvas?) {
            paint.apply {
                color = Color.RED
            }
            canvas?.drawCircle(getRealX(x), getRealY(y), lineInterval / 6.5875f, paint)
        }
    }

    enum class StoneViewType { BLACK, WHITE }

    inner class StoneView(private var type : StoneViewType, private var index : Int, private val x : Int, private val y : Int) : Element {
        val id = "$x/$y"

        fun setStoneType(type : StoneViewType) {
            this.type = type
        }

        fun getStoneType() : StoneViewType = type

        fun setStoneIndex(idx : Int) {
            index = idx
        }

        fun getStoneIndex() : Int = index

        override fun draw(canvas: Canvas?) {
            val radius = lineInterval / 2 - 2
            paint.apply {
                strokeWidth = (width / 351.3 + 0.5).toInt().toFloat()
                color = if(index == stones.size)
                    Color.parseColor(blendColors("#FFFFFF", boardSetting.lastStoneStrokeColor))
                else
                    Color.parseColor(blendColors("#FFFFFF", boardSetting.lineColor))
                style = Paint.Style.STROKE
            }
            canvas?.drawCircle(getRealX(x), getRealY(y), radius, paint)

            paint.style = Paint.Style.FILL
            if(type == StoneViewType.BLACK)
                paint.color = Color.BLACK
            else
                paint.color = Color.WHITE
            canvas?.drawCircle(getRealX(x), getRealY(y), radius - paint.strokeWidth / 2, paint)

            if(sequenceSetting.sequenceVisible && index - sequenceSetting.startPoint > 0) {
                paint.apply {
                    color = if (type == StoneViewType.BLACK)
                        Color.WHITE
                    else
                        Color.BLACK
                    textSize = lineInterval / 2.2f
                    typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                }
                val idxText = (index - sequenceSetting.startPoint).toString()
                paint.getTextBounds("333", 0, idxText.length, bounds)
                val centerX = bounds.exactCenterX()
                val centerY = bounds.exactCenterY()
                canvas?.drawText(idxText, getRealX(x) - centerX, getRealY(y) - centerY, paint)
            }
        }
    }

    inner class Child(private var text : String, private val x : Int, private val y : Int) : Element {
        override fun draw(canvas: Canvas?) {
            if (text.isNotEmpty()) {
                paint.color = Color.parseColor(blendColors("#FFFFFF", boardSetting.boardColor))
                paint.getTextBounds(text, 0, text.length, bounds)
                if(bounds.width() > lineInterval) {
                    text = text.substring(0, text.length - 1)
                    paint.getTextBounds(text, 0, text.length, bounds)
                }
                val centerX = bounds.exactCenterX()
                val centerY = bounds.exactCenterY()
                canvas?.drawRect(
                    getRealX(x) - centerX,
                    getRealY(y) - centerY + lineInterval / 13.2f,
                    getRealX(x) + centerX,
                    getRealY(y) + centerY - lineInterval / 13.2f, paint
                )
                paint.color = Color.parseColor(blendColors("#FFFFFF", boardSetting.textColor))
                paint.textSize = lineInterval / 2.2f
                paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                canvas?.drawText(
                    text,
                    getRealX(x) - centerX,
                    getRealY(y) - centerY,
                    paint
                )
            } else {
                paint.color = Color.parseColor(blendColors("#FFFFFF", boardSetting.nodeColor))
                canvas?.drawCircle(getRealX(x), getRealY(y), lineInterval / 34 * 6, paint)
            }
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
        updateBoardStatus(boardSetting, sequenceSetting)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        lineInterval = MeasureSpec.getSize(widthMeasureSpec).toFloat() / 16
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.isAntiAlias = true
        drawBoard(canvas)
        childs.forEach { child -> child.draw(canvas) }
        stones.keys.forEach { key -> stones[key]?.draw(canvas) }
    }

    private fun drawBoard(canvas : Canvas?) {
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

    fun placeStones(addStones : ArrayList<StoneView>?, deleteStonesID : ArrayList<String>?, childs : ArrayList<Child>) {
        if (deleteStonesID != null) {
            for (deleteStoneID in deleteStonesID) {
                stones.remove(deleteStoneID)
            }
        }

        if (addStones != null) {
            for(addStone in addStones) {
                if(stones.contains(addStone.id)) {
                    stones[addStone.id]!!.setStoneIndex(addStone.getStoneIndex())
                    stones[addStone.id]!!.setStoneType(addStone.getStoneType())
                } else {
                    stones[addStone.id] = addStone
                }
            }
        }

        this.childs.clear()
        this.childs.addAll(childs)
        invalidate()
    }

    fun removeAllStones() {
        stones.clear()
        childs.clear()
        invalidate()
    }

    fun getStoneID(x : Int, y : Int) : String = "$x/$y"

    private fun getRealX(x : Int) : Float = (x + 1) * lineInterval + lineInterval / 2

    private fun getRealY(y : Int) : Float = (y + 1) * lineInterval - lineInterval / 2

    fun updateBoardStatus(boardSetting : BoardSetting, sequenceSetting : SequenceSetting) {
        this.boardSetting = boardSetting
        this.sequenceSetting = sequenceSetting
        invalidate()
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
package com.renju_note.isoo.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.renju_note.isoo.RenjuEditApplication.Companion.boardSetting

class BoardLayout(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private var lineInterval : Float = 0f
    private var density : Float = 0f
    private val paint = Paint()
    private val coordinateViewID = ArrayList<Int>()
    private val stones = HashMap<String, Stone>()

    enum class StoneType {
        BLACK, WHITE, BLANK, LAST_BLACK, LAST_WHITE
    }

    inner class Stone(private var type : StoneType, private var text : String, private val x : Float, private val y : Float) {

        var viewID = -1
        var stone : TextView = TextView(context)

        fun getStoneType() : StoneType = type

        fun getText() : String = text

        fun setStoneType(type : StoneType) {
            this.type = type
            when(type) {
                StoneType.BLACK -> {
                    stone.background = stoneDrawable("#000000", boardSetting.lineColor, (width / 351.3 + 0.5).toInt())
                    stone.setTextColor(Color.WHITE)
                }
                StoneType.WHITE -> {
                    stone.background = stoneDrawable("#FFFFFF", boardSetting.lineColor, (width / 351.3 + 0.5).toInt())
                    stone.setTextColor(Color.BLACK)
                }
                StoneType.BLANK -> {
                    if(text.isBlank()) {
                        stone.background = stoneDrawable(boardSetting.lineColor, "#00000000", (lineInterval / 2).toInt())
                    } else {
                        when (text.length) {
                            3 -> stone.background = stoneDrawable(boardSetting.boardColor, "#00000000", (lineInterval / 4).toInt())
                            2 -> stone.background = stoneDrawable(boardSetting.boardColor, "#00000000", (lineInterval / 3).toInt())
                            else -> stone.background = stoneDrawable(boardSetting.boardColor, "#00000000", (lineInterval / 2).toInt())
                        }
                        stone.setTextColor(Color.parseColor(boardSetting.textColor))
                    }
                }
                StoneType.LAST_BLACK -> {
                    stone.background = stoneDrawable("#000000", boardSetting.lastStoneStrokeColor, (width / 351.3 + 0.5).toInt())
                    stone.setTextColor(Color.WHITE)
                }
                StoneType.LAST_WHITE -> {
                    stone.background = stoneDrawable("#FFFFFF", boardSetting.lastStoneStrokeColor, (width / 351.3 + 0.5).toInt())
                    stone.setTextColor(Color.BLACK)
                }
            }
        }

        fun setStoneText(text : String) {
            this.text = text
            stone.text = text
        }

        private fun initStone() {
            stone.setLines(1)
            stone.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            stone.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (lineInterval / 2.125 / density).toInt().toFloat())
            stone.gravity = Gravity.CENTER
            stone.id = View.generateViewId()
            viewID = stone.id
        }

        fun addStone(layout : BoardLayout) {
            val size = LayoutParams((lineInterval - 2).toInt(), (lineInterval - 2).toInt())
            val constraintSet = ConstraintSet()

            initStone()
            setStoneText(text)
            setStoneType(type)

            layout.addView(stone, 0, size)
            constraintSet.clone(layout)
            constraintSet.connect(stone.id, ConstraintSet.TOP, layout.id, ConstraintSet.TOP, y.toInt() - (lineInterval / 2 - 1).toInt())
            constraintSet.connect(stone.id, ConstraintSet.LEFT, layout.id, ConstraintSet.LEFT, x.toInt() - (lineInterval / 2 - 1).toInt())
            constraintSet.applyTo(layout)
        }

        private fun stoneDrawable(stoneColor: String, strokeColor: String, strokeSize : Int): GradientDrawable {
            val drawable = GradientDrawable()
            drawable.setStroke(strokeSize, Color.parseColor(strokeColor))
            drawable.setColor(Color.parseColor(stoneColor))
            drawable.shape = GradientDrawable.OVAL
            return drawable
        }

    }

    open class OnBoardTouchListener {
        open fun getCoordinates(x : Int, y : Int, realX : Float, realY : Float) {
            return
        }
    }

    private var onBoardTouchListener = OnBoardTouchListener()

    fun setOnBoardTouchListener(listener : OnBoardTouchListener) {
        onBoardTouchListener = listener
    }

    init {
        setBackgroundColor(Color.parseColor(boardSetting.boardColor))
        density = context.resources.displayMetrics.density
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        lineInterval = MeasureSpec.getSize(widthMeasureSpec).toFloat() / 16

        postDelayed({
            if(coordinateViewID.isNotEmpty()) {
                coordinateViewID.forEach { id -> this.removeViewInLayout(findViewById(id)) }
            }
            for (i in 0..14) {
                addCoordinate(i, 0)
                addCoordinate(i, 1)
            }
        }, 100)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.strokeWidth = (width / 351.3 + 0.5).toInt().toFloat()
        paint.color = Color.parseColor("#F2CA94")
        canvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        paint.color = Color.parseColor("#666666")
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
                    onBoardTouchListener.getCoordinates(xc, yc, x, y)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun addCoordinate(i : Int, mode : Int) {
        val size = LayoutParams((lineInterval - 2).toInt(), (lineInterval - 2).toInt())
        val constraintSet = ConstraintSet()
        val num = TextView(context)
        num.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (lineInterval / 2.125 / density).toInt().toFloat())
        num.gravity = Gravity.CENTER
        num.id = View.generateViewId()
        coordinateViewID.add(num.id)
        num.setTextColor(Color.parseColor(boardSetting.lineColor))
        num.typeface = Typeface.MONOSPACE
        this.addView(num, i, size)
        constraintSet.clone(this)
        if (mode == 0) { // number coordinate
            num.text = (i + 1).toString()
            constraintSet.connect(num.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP, (lineInterval * (15 - i) - lineInterval / 2).toInt() - (lineInterval / 2 - 1).toInt())
            constraintSet.connect(num.id, ConstraintSet.LEFT, this.id, ConstraintSet.LEFT, (lineInterval * 0.5).toInt() - (lineInterval / 2 - 1).toInt())
        } else if (mode == 1) { // character coordinate
            num.text = (i + 65).toChar().toString()
            constraintSet.connect(num.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP, (lineInterval * 15.5).toInt() - (lineInterval / 2 - 1).toInt())
            constraintSet.connect(num.id, ConstraintSet.LEFT, this.id, ConstraintSet.LEFT, (lineInterval * (i + 1) + lineInterval / 2).toInt() - (lineInterval / 2 - 1).toInt())
        }
        constraintSet.applyTo(this)
    }

    fun placeStones(addStones : ArrayList<Pair<String, Stone>>?, deleteStonesID : ArrayList<String>?) {
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
                    stones[addStone.first]!!.setStoneType(addStone.second.getStoneType())
                    stones[addStone.first]!!.setStoneText(addStone.second.getText())
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

    fun generateStoneID(x : Int, y : Int) : String {
        return "$x/$y"
    }

}
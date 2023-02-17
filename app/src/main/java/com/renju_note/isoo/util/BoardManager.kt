package com.renju_note.isoo.util

interface BoardManager {

    enum class ElementType {
        BLACK, WHITE, CHILD
    }

    fun getNowBoardStatus() : Array<Array<String>>

    fun setNowTextBoxString(text : String)

    fun getNowTextBoxString() : String

    fun getSequence() : ArrayList<Pair<Int, Int>>

    fun getChangeStatus(before : Array<Array<String>>, after : Array<Array<String>>) : Pair<ArrayList<String>, ArrayList<String>>

    fun generateInfoString(type : ElementType, x : Int, y : Int, text : String) : String

    fun infoString2Info(infoString : String) : Pair<Pair<Int, Int>, Pair<ElementType, String>>

    fun getNowIndex() : Int

    fun undo() : Boolean

    fun undoAll() : Boolean

    fun redo() : Boolean

    fun deleteBranch() : Boolean

    fun putStone(x : Int, y : Int) : Boolean

    fun addNewChild(x : Int, y : Int, text : String) : Boolean

    fun <T> loadNodes(tree : T)

}
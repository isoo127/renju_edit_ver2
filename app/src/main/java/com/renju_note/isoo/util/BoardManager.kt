package com.renju_note.isoo.util

interface BoardManager {

    enum class ElementType {
        BLACK, WHITE, CHILD
    }

    fun getNowBoardStatus() : Array<Array<String>>

    fun getSequence() : String

    fun getChangeStatus() : Pair<ArrayList<String>, ArrayList<String>>

    fun generateInfoString(type : ElementType, x : Int, y : Int, text : String) : String

    fun getNowIndex() : Int

    fun undo()

    fun redo()

    fun deleteBranch()

    fun loadNodes()

}
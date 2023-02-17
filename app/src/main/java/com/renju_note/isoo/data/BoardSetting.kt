package com.renju_note.isoo.data

import com.renju_note.isoo.util.PreferenceUtil

data class BoardSetting (
    var boardColor : String,
    var lineColor : String,
    var lastStoneStrokeColor : String,
    var textColor : String,
    var nodeColor : String,
    var sequenceVisible : Boolean,
    var startPoint : Int
) {

    companion object {
        fun getDefaultSetting() : BoardSetting {
            val boardColor = "#F2CA94"
            val lineColor = "#666666"
            val lastStoneStrokeColor = "#D32560"
            val textColor = "#0000FF"
            val nodeColor = "#0000FF"
            val sequenceVisible = true
            val startPoint = 0
            return BoardSetting(boardColor, lineColor, lastStoneStrokeColor, textColor, nodeColor, sequenceVisible, startPoint)
        }
    }

    fun save(pref : PreferenceUtil) {
        pref.setString("boardColor", boardColor)
        pref.setString("lineColor", lineColor)
        pref.setString("lastStoneStrokeColor", lastStoneStrokeColor)
        pref.setString("textColor", textColor)
        pref.setString("nodeColor", nodeColor)
        pref.setString("sequenceVisible", sequenceVisible.toString())
        pref.setString("startPoint", startPoint.toString())
    }

    fun load(pref : PreferenceUtil) {
        boardColor = pref.getString("boardColor", "#F2CA94")
        lineColor = pref.getString("lineColor", "#666666")
        lastStoneStrokeColor = pref.getString("lastStoneStrokeColor", "#D32560")
        textColor = pref.getString("textColor", "#0000FF")
        nodeColor = pref.getString("nodeColor", "#0000FF")
        sequenceVisible = pref.getString("sequenceVisible", "true").toBoolean()
        startPoint = pref.getString("startPoint", "0").toInt()
    }

}

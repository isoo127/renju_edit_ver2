package com.renju_note.isoo.data

import com.renju_note.isoo.util.PreferenceUtil

data class SequenceSetting (
    var sequenceVisible : Boolean,
    var startPoint : Int
) {

    companion object {
        fun getDefaultSetting() : SequenceSetting {
            val sequenceVisible = true
            val startPoint = 0
            return SequenceSetting(sequenceVisible, startPoint)
        }
    }

    fun save(pref : PreferenceUtil) {
        pref.setString("sequenceVisible", sequenceVisible.toString())
        pref.setString("startPoint", startPoint.toString())
    }

    fun load(pref : PreferenceUtil) {
        sequenceVisible = pref.getString("sequenceVisible", "true").toBoolean()
        startPoint = pref.getString("startPoint", "0").toInt()
    }

}

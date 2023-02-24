package com.renju_note.isoo.data

import com.renju_note.isoo.util.PreferenceUtil

data class SequenceSetting (
    var sequenceVisible : Boolean,
) {

    var startPoint = 0

    companion object {
        fun getDefaultSetting() : SequenceSetting {
            val sequenceVisible = true
            return SequenceSetting(sequenceVisible)
        }
    }

    fun save(pref : PreferenceUtil) {
        pref.setString("sequenceVisible", sequenceVisible.toString())
    }

    fun load(pref : PreferenceUtil) {
        sequenceVisible = pref.getString("sequenceVisible", "true").toBoolean()
        startPoint = 0
    }

}

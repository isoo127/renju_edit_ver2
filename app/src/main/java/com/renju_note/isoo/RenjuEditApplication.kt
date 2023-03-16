package com.renju_note.isoo

import android.app.Application
import com.renju_note.isoo.data.BoardSetting
import com.renju_note.isoo.data.ModeSetting
import com.renju_note.isoo.data.SequenceSetting
import com.renju_note.isoo.data.TextAreaSetting
import com.renju_note.isoo.util.PreferenceUtil
import com.renju_note.isoo.util.SeqTreeBoardManager

class RenjuEditApplication : Application() {

    class Settings {
        var boardSetting = BoardSetting.getDefaultSetting()
        var sequenceSetting = SequenceSetting.getDefaultSetting()
        var textAreaSetting = TextAreaSetting.getDefaultSetting()
        var modeSetting = ModeSetting.getDefaultSetting()

        fun save(pref : PreferenceUtil) {
            boardSetting.save(pref)
            sequenceSetting.save(pref)
            textAreaSetting.save(pref)
            modeSetting.save(pref)
        }

        fun load(pref : PreferenceUtil) {
            boardSetting.load(pref)
            sequenceSetting.load(pref)
            textAreaSetting.load(pref)
            modeSetting.load(pref)
        }

        fun setDefaultSetting() {
            boardSetting = BoardSetting.getDefaultSetting()
            sequenceSetting.sequenceVisible = true
            textAreaSetting = TextAreaSetting.getDefaultSetting()
            modeSetting = ModeSetting.getDefaultSetting()
        }
    }

    companion object {
        lateinit var pref : PreferenceUtil
        var settings = Settings()
        var boardManager = SeqTreeBoardManager()
    }

    override fun onCreate() {
        super.onCreate()
        pref = PreferenceUtil(applicationContext)
        settings.load(pref)
    }

}
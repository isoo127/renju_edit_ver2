package com.renju_note.isoo

import android.app.Application
import com.renju_note.isoo.data.BoardSetting
import com.renju_note.isoo.util.PreferenceUtil

class RenjuEditApplication : Application() {

    companion object {
        lateinit var pref : PreferenceUtil
        var boardSetting = BoardSetting.getDefaultSetting()
    }

    override fun onCreate() {
        super.onCreate()
        pref = PreferenceUtil(applicationContext)
        boardSetting.load(pref)
    }

}
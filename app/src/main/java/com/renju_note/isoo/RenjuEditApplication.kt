package com.renju_note.isoo

import android.app.Application
import com.renju_note.isoo.data.*
import com.renju_note.isoo.util.PreferenceUtil
import com.renju_note.isoo.util.SeqTreeBoardManager
import io.realm.Realm
import io.realm.RealmConfiguration

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
        var editingFile : StorageElement? = null
    }

    override fun onCreate() {
        super.onCreate()
        pref = PreferenceUtil(applicationContext)
        settings.load(pref)

        Realm.init(this)
        val config : RealmConfiguration = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .name("renju_edit.realm")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

}
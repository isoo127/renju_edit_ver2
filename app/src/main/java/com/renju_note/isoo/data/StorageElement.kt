package com.renju_note.isoo.data

import android.net.Uri
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class StorageElement(
    var title : String = "",
    @Ignore
    val uri : Uri? = null,
    @Ignore
    val seq : ArrayList<Stone>? = null
    ) : RealmObject() {

    var date : String = ""
    var sequence = RealmList<String>()
    @PrimaryKey
    var location : String = ""

    init {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = current.format(formatter)
        date = formatted

        if(seq != null) {
            for (stone in seq) {
                val str = stone.x.toString() + "/" + stone.y.toString()
                sequence.add(str)
            }
        }

        location = uri.toString()
    }

    fun getParsedUri() : Uri {
        return Uri.parse(location)
    }

}
package com.renju_note.isoo.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.renju_note.isoo.R


class LoadingDialog(context : Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val gifImageView = findViewById<ImageView>(R.id.loading_progress_bar)
        Glide.with(context).asGif().load(R.raw.loading).into(gifImageView)
    }

}






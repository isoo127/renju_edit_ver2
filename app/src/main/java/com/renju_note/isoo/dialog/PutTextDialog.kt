package com.renju_note.isoo.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.renju_note.isoo.databinding.DialogPutTextBinding

class PutTextDialog(context: Context) : Dialog(context) {

    private lateinit var binding : DialogPutTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogPutTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.putTextOkBtn.setOnClickListener {
            responseListener?.ok(binding.putTextAreaEt.text.toString())
        }
        binding.putTextCancelBtn.setOnClickListener {
            responseListener?.cancel()
        }
    }

    interface OnResponseListener {
        fun cancel()
        fun ok(text : String)
    }

    private var responseListener : OnResponseListener? = null

    fun setOnResponseListener(listener : OnResponseListener) {
        responseListener = listener
    }

}
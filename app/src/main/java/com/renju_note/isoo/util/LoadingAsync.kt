package com.renju_note.isoo.util

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.renju_note.isoo.dialog.LoadingDialog


class LoadingAsync(val context : Context) {

     class EndHandler : Handler(Looper.getMainLooper()) {
         private var processListener : ProcessListener? = null
         private var loading : LoadingDialog? = null

         override fun handleMessage(msg: Message) {
             loading?.dismiss()
             if(msg.data.getBoolean("failed")) {
                 processListener?.whenFailed()
             } else {
                 processListener?.whenFinished()
             }
         }

         fun setProcess(processListener : ProcessListener, loading : LoadingDialog) {
             this.processListener = processListener
             this.loading = loading
         }
    }

    private val endHandler = EndHandler()

    interface ProcessListener {
        fun process()
        fun whenFinished()
        fun whenFailed()
    }

    private var processListener : ProcessListener? = null

    fun setProcess(processListener : ProcessListener) {
        this.processListener = processListener
        endHandler.setProcess(processListener, loading)
    }

    private val loading = LoadingDialog(context)

    fun run() {
        loading.show()
        var failed = false
        Thread {
            try {
                processListener?.process()
            } catch(e : Exception) {
                e.printStackTrace()
                failed = true
            }
            val message: Message = endHandler.obtainMessage()
            val bundle = Bundle()
            bundle.putBoolean("failed", failed)
            message.data = bundle
            endHandler.sendMessage(message)
        }.start()
    }

}
package org.michaelbel.tjgram.utils.qrscanner.decode

import android.app.Activity
import android.content.DialogInterface

class FinishListener internal constructor(
    private val mActivityToFinish: Activity) : DialogInterface.OnClickListener, DialogInterface.OnCancelListener, Runnable {

    override fun onCancel(dialogInterface: DialogInterface) {
        run()
    }

    override fun onClick(dialogInterface: DialogInterface, i: Int) {
        run()
    }

    override fun run() {
        mActivityToFinish.finish()
    }
}
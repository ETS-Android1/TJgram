package com.blikoon.qrcodescanner.decode

import android.app.AlertDialog
import android.content.Context

import org.michaelbel.tjgram.R

class DecodeManager {

    fun showCouldNotReadQrCodeFromScanner(context: Context, listener: OnRefreshCameraListener?) {
        AlertDialog.Builder(context).setTitle(R.string.app_name)
            .setMessage(R.string.msg_qr_code_not_read)
            .setPositiveButton(R.string.action_ok) { dialog, _ ->
                dialog.dismiss()
                listener?.refresh()
            }.show()
    }

    interface OnRefreshCameraListener {
        fun refresh()
    }
}
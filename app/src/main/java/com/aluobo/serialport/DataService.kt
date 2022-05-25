package com.aluobo.serialport

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DataService : Service() {
    private lateinit var serialPortHelper: SerialPortHelper
    lateinit var serialDataFlow: Flow<ByteArray>

    fun openSerial() {
        serialPortHelper = SerialPortHelper("tty3", 0)
        serialPortHelper.onDataReceived = ::handlerData
    }

    override fun onBind(intent: Intent?): IBinder? {
        return MineBinder()
    }

    private fun handlerData(byteArray: ByteArray, size: Int) {
        serialDataFlow = flow {
            emit(byteArray)
        }
    }

    inner class MineBinder : Binder() {
        fun getService(): DataService = this@DataService
    }

}
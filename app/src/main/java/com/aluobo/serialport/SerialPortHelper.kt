package com.aluobo.serialport

import android_serialport_api.SerialPort
import kotlinx.coroutines.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream


class SerialPortHelper(val port: String, val baudRate: Int) {
    var isOpen = false
        private set
    lateinit var outputStream: OutputStream
    lateinit var inputStream: InputStream

    lateinit var serialPort: SerialPort

    fun open() {
        serialPort = SerialPort(File(port), baudRate, 0)
        outputStream = serialPort.outputStream
        inputStream = serialPort.inputStream
        isOpen = true
        receiveJob.start()
    }

    fun send(data: ByteArray) {
        outputStream.write(data)
    }

    /**
     * 用一个协程任务来一直获取串口信息
     */
    private val receiveJob =
        GlobalScope.async(start = CoroutineStart.LAZY, context = Dispatchers.IO) {
            while (true) {
                val available = inputStream.available()
                if (available > 0) {
                    val buffer = ByteArray(available)
                    val size = inputStream.read(buffer)
                    if (size > 0) {
                        onDataReceived(buffer, size)
                    }
                } else {
                    delay(50)
                }
            }
        }

    fun close() {
        receiveJob.cancel()
        serialPort.close()
        isOpen = false
    }

    lateinit var onDataReceived: (ByteArray, Int) -> Unit
}
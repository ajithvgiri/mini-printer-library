package com.ajithvgiri.miniprinter

import android.bluetooth.BluetoothSocket
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.util.Log
import java.io.IOException
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.text.DateFormat
import java.util.*

class PrinterCommands(private val mmSocket: BluetoothSocket) {


    //print custom
    private fun printCustom(msg: String, size: Int, align: Int) {
        //Print config "mode"
        val cc = byteArrayOf(0x1B, 0x21, 0x03)  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        val bb = byteArrayOf(0x1B, 0x21, 0x08)  // 1- only bold text
        val bb2 = byteArrayOf(0x1B, 0x21, 0x20) // 2- bold with medium text
        val bb3 = byteArrayOf(0x1B, 0x21, 0x10) // 3- bold with large text
        try {
            when (size) {
                0 -> mmSocket.outputStream.write(cc)
                1 -> mmSocket.outputStream.write(bb)
                2 -> mmSocket.outputStream.write(bb2)
                3 -> mmSocket.outputStream.write(bb3)
            }

            when (align) {
                0 ->
                    //left align
                    mmSocket.outputStream.write(Commands.ESC_ALIGN_LEFT)
                1 ->
                    //center align
                    mmSocket.outputStream.write(Commands.ESC_ALIGN_CENTER)
                2 ->
                    //right align
                    mmSocket.outputStream.write(Commands.ESC_ALIGN_RIGHT)
            }
            mmSocket.outputStream.write(msg.toByteArray())
            mmSocket.outputStream.write(Commands.LF.toInt())
            //mmOutputStream.write(cc);
            //printNewLine();
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //print photo
    private fun printPhoto(resources: Resources, img: Int) {
        try {
            val bitmap = BitmapFactory.decodeResource(resources, img)
            if (bitmap != null) {
                val command = Utils.decodeBitmap(bitmap)
                printText(command)
            } else {
                Log.e("Print Photo error", "the file isn't exists")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PrintTools", "the file isn't exists")
        }

    }

    //print unicode
    fun printUnicode() {
        try {
            mmSocket.outputStream.write(Commands.ESC_ALIGN_CENTER)
            printText(Utils.UNICODE_TEXT)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //print new line
    private fun printNewLine() {
        try {
            mmSocket.outputStream.write(Commands.FEED_LINE)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //print text
    private fun printText(msg: String) {
        try {
            // Print normal text
            mmSocket.outputStream.write(msg.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //print byte[]
    private fun printText(msg: ByteArray) {
        try {
            // Print normal text
            mmSocket.outputStream.write(msg)
            printNewLine()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    private fun dateandTime(str1: String, str2: String): String {
        var ans = str1 + str2
        val n = 20 - str1.length + str2.length
        ans = str1 + String(CharArray(n)).replace("\u0000", " ") + str2
        return ans
    }

    private fun printItems(str1: String, str2: String, str3: String): String {
        var ans = str1 + str2 + str3
        val lengthofspace = 39 - ans.length
        ans = str1 + " : " + str2 + String(CharArray(lengthofspace)).replace("\u0000", " ") + str3
        return ans
    }


    private fun leftRightAlign(str1: String, str2: String): String {
        var ans = str1 + str2
        val n = 30 - str1.length + str2.length
//        val n = 30 - str1.length + str2.length
        ans = str1 + String(CharArray(n)).replace("\u0000", " ") + str2
        return ans
    }

    private fun lefAlign(str1: String): String {
        var ans = str1
        val n = 40 - str1.length
//        val n = 30 - str1.length + str2.length
        ans = str1 + String(CharArray(n)).replace("\u0000", " ")
        return ans
    }


    private fun getDateTime(): Array<String?> {
        //        final Calendar c = Calendar.getInstance();
        //        String stringDate = DateFormat.getDateTimeInstance().format(date);
        //        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        val dateTime = arrayOfNulls<String>(2)
        val date = Date()
        //        dateTime[1] = c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE);
        dateTime[0] = DateFormat.getDateInstance().format(date)
        dateTime[1] = DateFormat.getTimeInstance().format(date)
        return dateTime
    }

    fun killPrinter() {
        try {
            if (mmSocket != null) {
                if (mmSocket.outputStream != null) {
                    mmSocket.outputStream.close()
                    mmSocket.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
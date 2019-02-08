package com.ajithvgiri.receiptprinter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ajithvgiri.miniprinter.PrinterCommands
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    // needed for communication to bluetooth device / network
    lateinit var mmOutputStream: OutputStream
    // android built in classes for bluetooth operations
    var mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    lateinit var mmSocket: BluetoothSocket
    lateinit var mmDevice: BluetoothDevice
    lateinit var mmInputStream: InputStream


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBT()

        button.setOnClickListener {
            Toast.makeText(this,"Printing...",Toast.LENGTH_LONG).show()
            PrinterCommands(mmSocket).printText("Hello worldr")
        }
    }


    /*      Bluetooth Printer */

    // this will find a bluetooth printer device
    private fun searchBT() {
        try {
            val pairedDevices = mBluetoothAdapter.bondedDevices

            if (pairedDevices.size > 0) {
                for (device in pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.name.equals("BlueTooth Printer", false) || device.name.equals("BTprinter3025", false)) {
                        mmDevice = device
                        openBT()
                        break
                    }
                }
            }

            Log.d(TAG, "Bluetooth devices found " + mmDevice.let { it.name })

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // tries to open a connection to the bluetooth printer device
    @Throws(IOException::class)
    private fun openBT() {
        try {

            // Standard SerialPortService ID
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid)
            mmSocket.connect()
            mmOutputStream = mmSocket.outputStream
            mmInputStream = mmSocket.inputStream


            Log.d(TAG, "Bluetooth Opened")
            //Toast.makeText(this, "Printer Connected", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, e.message)
            //Toast.makeText(this, "Printer Not Connected", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        PrinterCommands(mmSocket).killPrinter()
    }

}

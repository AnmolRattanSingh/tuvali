package io.mosip.tuvali.ble.central.impl

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import io.mosip.tuvali.transfer.Util.Companion.getLogTag
import java.util.*

class Scanner(context: Context) {
    private val logTag = getLogTag(javaClass.simpleName)
    private lateinit var onScanStartFailure: (Int) -> Unit
    private lateinit var onDeviceFound: (ScanResult) -> Unit
    private lateinit var onAvailableDevicesFound: (List<BluetoothDevice>) -> Unit
    private var bluetoothLeScanner: BluetoothLeScanner
    private val foundDevices: MutableSet<BluetoothDevice> = mutableSetOf()

    init {
        val bluetoothManager: BluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d(logTag, "Found the device: ${result.device}. The bytes are: ${result.scanRecord?.bytes}")
            if (foundDevices.add(result.device)) {
                onDeviceFound(result)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(logTag, "Scan failed with error: $errorCode")
            onScanStartFailure(errorCode)
        }
    }

    private val leViewScannedCallback: ScanCallback = object : ScanCallback() {
        override fun onBatchScanResults(results: List<ScanResult>) {
                results.forEach { result ->
                    Log.d(logTag, "le Batch found device: ${result.device}. The name is: ${result.device.name}")
                    foundDevices.add(result.device)
                }
                onAvailableDevicesFound(foundDevices.toList())
            }

        override fun onScanFailed(errorCode: Int) {
            Log.e(logTag, "le failed scan: $errorCode")
            onAvailableDevicesFound(emptyList())
        }
    }

    @SuppressLint("MissingPermission")
    fun start(
        serviceUUID: UUID,
        onDeviceFound: (ScanResult) -> Unit,
        onScanStartFailure: (Int) -> Unit
    ) {
        this.onDeviceFound = onDeviceFound
        this.onScanStartFailure = onScanStartFailure

        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(serviceUUID))
            .build()

        bluetoothLeScanner.startScan(
            listOf(filter),
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
            leScanCallback
        )
    }

    @SuppressLint("MissingPermission")
    fun viewAvailableConnections(serviceUUID: UUID, onAvailableDevicesFound: (List<BluetoothDevice>) -> Unit) {
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(serviceUUID))
            .build()
        
        this.onAvailableDevicesFound = onAvailableDevicesFound

        Log.d(logTag, "le viewAvailableConnections in Scanner.kt")
        bluetoothLeScanner.startScan(
            listOf(filter),
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(2000).build(),
            leViewScannedCallback
        )
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        bluetoothLeScanner.stopScan(leScanCallback)
    }
}

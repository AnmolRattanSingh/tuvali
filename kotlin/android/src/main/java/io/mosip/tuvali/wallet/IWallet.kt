package io.mosip.tuvali.wallet

import io.mosip.tuvali.common.events.Event
import android.bluetooth.BluetoothDevice

interface IWallet {
  fun startConnection(uri: String)
  fun viewAvailableConnections(callback: (List<BluetoothDevice>) -> Unit)
  fun sendData(payload: String)
  fun disconnect()
  fun subscribe(listener: (Event) -> Unit)
  fun unSubscribe()
  fun handleDisconnect(status: Int, newState: Int)
}

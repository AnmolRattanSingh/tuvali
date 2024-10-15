package io.mosip.tuvali.ble.central.state.message
import android.bluetooth.BluetoothDevice

import java.util.*

class ViewAvailableConnectionsMessage(val serviceUUID: UUID, val callback: (List<BluetoothDevice>) -> Unit): IMessage(
  CentralStates.VIEW_AVAILABLE_CONNECTIONS
)

package com.ble.central.state.message

import java.util.*

class SubscribeSuccessMessage(val charUUID: UUID) : IMessage(CentralStates.SUBSCRIBE_SUCCESS)

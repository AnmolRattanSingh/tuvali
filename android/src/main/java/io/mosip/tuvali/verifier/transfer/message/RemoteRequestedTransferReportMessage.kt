package io.mosip.tuvali.verifier.transfer.message

class RemoteRequestedTransferReportMessage(val semaphoreCharValue: Int): IMessage(TransferMessageTypes.REMOTE_REQUESTED_TRANSFER_REPORT) {}

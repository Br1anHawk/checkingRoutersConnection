fun statusConverterToString(status: RouterStatus): String {
    var statusInStringFormat = ""
    statusInStringFormat = when(status) {
        RouterStatus.NONE -> ""
        RouterStatus.OFFLINE -> "disconnected"
        RouterStatus.ONLINE -> "ok"
    }
    return statusInStringFormat
}

const val DEFAULT_CHECKING_POOL_SIZE = 25
fun statusConverterToString(status: RouterStatus): String {
    var statusInStringFormat = ""
    statusInStringFormat = when(status) {
        RouterStatus.NONE -> ""
        RouterStatus.OFFLINE -> "disconnected"
        RouterStatus.ONLINE -> "ok"
    }
    return statusInStringFormat
}
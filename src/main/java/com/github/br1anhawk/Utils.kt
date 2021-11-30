package com.github.br1anhawk

fun statusConverterToString(status: RouterStatus): String {
    var statusInStringFormat = ""
    statusInStringFormat = when(status) {
        RouterStatus.NONE -> ""
        RouterStatus.OFFLINE -> "disconnected"
        RouterStatus.ONLINE -> "ok"
    }
    return statusInStringFormat
}

const val DEFAULT_CHECKING_POOL_SIZE = 250
const val DEFAULT_COUNT_OF_ATTEMPTED = 3
const val OS_WINDOWS_NAME = "Windows"
const val OS_LINUX_NAME = "Linux"
const val LOGGING = true
const val LOGGING_FILE_NAME = "logging.log"
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


fun main() {
    CoroutineScope(Dispatchers.Main).launch {
        println(ping("128.65.22.228"))
        delay(10)
    }
    CoroutineScope(Dispatchers.Main).launch {
        println(ping("1.1.1.1"))
        delay(10)
    }
    CoroutineScope(Dispatchers.Main).launch {
        println(ping("127.0.0.1"))
        delay(10)
    }
    CoroutineScope(Dispatchers.Main).launch {
        println(ping("142.250.186.46"))
        delay(10)
    }




}

fun ping(host: String): Boolean {
    //var host = "128.65.22.228"
    //host = "142.250.186.46"

    val command = "ping"
    val attributes = arrayOf("-c 1", "-s 32") //LINUX
    val processBuilder = ProcessBuilder(command, host, *attributes)
    val process = processBuilder.start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val stringBuilder = StringBuilder()
    var line: String?
    while (reader.readLine().also { line = it } != null) {
        stringBuilder.appendLine(line)
    }
    //print(stringBuilder.toString())
    var isReachable = false
    if (stringBuilder.contains(Regex("="))) {
        isReachable = true
    }
    return isReachable
}
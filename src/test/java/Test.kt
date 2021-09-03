import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader


fun main() {
//    runBlocking {
//        launch {
//            print(ping("128.65.22.228"))
//            println(" 128.65.22.228")
//            //delay(10)
//        }
//        launch {
//            print(ping("1.2.1.1"))
//            println(" 1.2.1.1")
//            //delay(10)
//        }
//        launch {
//            delay(5000)
//            print(ping("127.0.0.1"))
//            println(" 127.0.0.1")
//            //delay(10)
//        }
//        launch {
//            print(ping("142.250.186.46"))
//            println(" 142.250.186.46")
//            //delay(10)
//        }
//    }

    val hosts = arrayListOf<String>()
    //hosts.add("")
    hosts.add("128.65.22.228")
    hosts.add("1.2.1.1")
    hosts.add("127.0.0.1")
    hosts.add("youtube.com")
    hosts.add("reituio.com")
    hosts.add("twitch.tv")
    hosts.add("vk.com")
    hosts.add("ru.ru")
    hosts.add("5.3.5.6.7.4")
    hosts.add("34.23.12.245")
    hosts.add("onliner.by")
    hosts.add("mail.ru")
    hosts.add("yandex.by")
    hosts.add("123.123.123.123")
    hosts.add("google.com")
    hosts.add("sdkjgkldjgkljkl.ru")
    hosts.add("stackowerflow.com")
    hosts.add("github.com")
    hosts.add("dfjgkhj.by")
    hosts.add("sdkjhg.com")


    val connectionChecker = ConnectionChecker()
    connectionChecker.loadHosts(hosts)
    val timeStart = System.currentTimeMillis()
    connectionChecker.checkAllHostsConnection()
    val timeEnd = System.currentTimeMillis()
    println("TIME DURATION -------   " + (timeEnd - timeStart) / 1000 + "s")



}

fun ping(host: String): Boolean {
    //var host = "128.65.22.228"
    //host = "142.250.186.46"

    val command = "ping"
    val attributes = arrayOf("-c 1", "-s 32") //LINUX
    val processBuilder = ProcessBuilder(command, host, *attributes)
    val process = processBuilder.start()
    process.waitFor()
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
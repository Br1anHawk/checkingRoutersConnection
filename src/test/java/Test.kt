import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Inet4Address


fun main() {
    var ia = Inet4Address.getByName("128.65.22.228")
    ia = Inet4Address.getByName("142.250.186.46")
    //ia = Inet4Address.getLocalHost()
    if (ia.isReachable(3000)) {
        print(ia.hostAddress)
    }

    val command = "ping"
    val host = "128.65.22.228"
    val attributes = arrayOf("-c 1", "-s 32")
    val pb = ProcessBuilder(command, host, *attributes)
    val process = pb.start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    var line: String?
    while (reader.readLine().also { line = it } != null) {
        println(line)
    }



}
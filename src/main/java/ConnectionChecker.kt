import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

class ConnectionChecker {
    private val routers: ArrayList<Router> = arrayListOf()

    fun getRouters(): Array<Router> {
        return routers.toTypedArray()
    }

    fun loadHosts(hosts: ArrayList<String>) {
        hosts.forEach { routers.add(Router(it)) }
    }

    fun checkAllHostsConnection() {
        if (routers.isEmpty()) return
        val routersQueue: Queue<Router> = LinkedList()
        routers.forEach { routersQueue.add(it) }
        while (routersQueue.isNotEmpty()) {
            val routersPool = getRoutersPool(routersQueue)
            runBlocking {
                while (routersPool.isNotEmpty()) {
                    val router = routersPool.poll()
                    launch {
                        ping(router)
                    }
                }
            }
        }
        printRoutersInfo() //ONLY FOR DEBUGGING
    }

    private fun getRoutersPool(routersQueue: Queue<Router>): Queue<Router> {
        val maxPoolSize = 25;
        val routersPool: Queue<Router> = LinkedList()
        while(routersQueue.isNotEmpty()) {
            routersPool.add(routersQueue.poll())
            if (routersPool.size == maxPoolSize) break
        }
        return routersPool
    }

    private fun ping(router: Router) {
        val command = "ping"
        //val attributes = arrayOf("-n 1", "-l 1") //WINDOWS
        val attributes = arrayOf("-c 1", "-s 32") //LINUX
        val processBuilder = ProcessBuilder(command, router.host, *attributes)
        val process = processBuilder.start()
        runBlocking {
            launch {
                process.waitFor()
            }
        }
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.appendLine(line)
        }
        print(stringBuilder.toString()) //ONLY FOR DEBUGGING
        if (stringBuilder.contains(Regex("="))) {
            router.status = RouterStatus.ONLINE
        } else {
            router.status = RouterStatus.OFFLINE
        }
    }

    private fun printRoutersInfo() {
        routers.forEach { println(it) }
    }
}
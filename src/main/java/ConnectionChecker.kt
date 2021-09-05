import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import javax.swing.JProgressBar
import kotlin.collections.ArrayList

class ConnectionChecker {
    private val routers: ArrayList<Router> = arrayListOf()

    var maxPoolSize = DEFAULT_CHECKING_POOL_SIZE

    private var lastTimeCheckingDurationInMs = 0L

    fun getLastTimeCheckingDurationInMs(): Long {
        return lastTimeCheckingDurationInMs
    }

    fun getRouters(): Array<Router> {
        return routers.toTypedArray()
    }

    fun loadHosts(hosts: ArrayList<String>) {
        routers.clear()
        hosts.forEach { routers.add(Router(it)) }
    }

    fun checkAllHostsConnection(progressBar: JProgressBar) {
        if (routers.isEmpty()) return
        val routersQueue: Queue<Router> = LinkedList()
        routers.forEach { routersQueue.add(it) }
        val timeStartChecking = System.currentTimeMillis()
        while (routersQueue.isNotEmpty()) {
            val routersPool = getRoutersPool(routersQueue)
            runBlocking {
                while (routersPool.isNotEmpty()) {
                    val router = routersPool.poll()
                    launch {
                        ping(router)
                        progressBar.value++
                        //println("PROGRESS BAR VALUE ----  " + progressBar.value)
                    }
                }
            }
        }
        val timeEndChecking = System.currentTimeMillis()
        lastTimeCheckingDurationInMs = timeEndChecking - timeStartChecking
        printRoutersInfo() //ONLY FOR DEBUGGING
    }

    private fun getRoutersPool(routersQueue: Queue<Router>): Queue<Router> {
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
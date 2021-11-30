package com.github.br1anhawk

import kotlinx.coroutines.*
import org.apache.xpath.operations.Bool
import sun.rmi.runtime.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import javax.swing.JProgressBar
import kotlin.collections.ArrayList

class ConnectionChecker {
    private val routers: ArrayList<Router> = arrayListOf()

    var maxPoolSize = DEFAULT_CHECKING_POOL_SIZE
    var countOfAttempted = DEFAULT_COUNT_OF_ATTEMPTED
    var logging = Logging()

    fun setLogging(logging: Boolean) {
        this.logging.isLogging = logging
    }

    private var lastTimeCheckingDurationInMs = 0L

    var os = OperatingSystem.WINDOWS

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
                val jobs: MutableList<Job> = mutableListOf()
                while (routersPool.isNotEmpty()) {
                    val router = routersPool.poll()
                    val job = launch {
                        ping(router)
                        progressBar.value++

                        //println("PROGRESS BAR VALUE ----  " + progressBar.value) //DEBUGGING

                    }
                    jobs.add(job)
                    //println(jobs.size) //DEBUGGING
                }
                jobs.joinAll()
            }
        }

//        val pbs: Queue<ProcessBuilder> = LinkedList()
//        routers.forEach {
//            pbs.add(ping(it))
//        }
//        runBlocking {
//            var count = 0
//            val jobs: MutableList<Job> = mutableListOf()
//            while (pbs.isNotEmpty()) {
//
//                val job = launch {
//                    val process = pbs.poll().start()
//
//                    runBlocking {
//                        launch {
//                            process.waitFor()
//                            val reader = BufferedReader(InputStreamReader(process.inputStream))
//                            val stringBuilder = StringBuilder()
//                            var line: String?
//                            while (reader.readLine().also { line = it } != null) {
//                                stringBuilder.appendLine(line)
//                            }
//
//                            //print(stringBuilder.toString()) //ONLY FOR DEBUGGING
//                            logging.log(stringBuilder.toString())
//
//                            if (stringBuilder.contains(Regex("time="))) {
//                                //router.status = RouterStatus.ONLINE
//                            } else {
//                                //router.status = RouterStatus.OFFLINE
//                            }
//                            progressBar.value++
//                        }
//                    }
//
//
//
//                }
//                count++
//                jobs.add(job)
//                if (count >= maxPoolSize) {
//                    //jobs.joinAll()
//                    count = 0
//                    jobs.clear()
//                }
//            }
//        }



        val timeEndChecking = System.currentTimeMillis()
        lastTimeCheckingDurationInMs = timeEndChecking - timeStartChecking

        //printRoutersInfo() //ONLY FOR DEBUGGING
        routers.forEach { logging.log(it.toString()) }

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
        val function = "ping"
        //val attributes = arrayOf("-n 1", "-l 1") //WINDOWS
        //var attributes = arrayOf("-c 1", "-s 32") //LINUX
        val command = when (os) {
            OperatingSystem.WINDOWS -> arrayOf(
                "cmd.exe", "/c", "chcp", "65001", "&",
                function, router.host, "-n", countOfAttempted.toString(), "-l", "1"
            )
            OperatingSystem.LINUX -> arrayOf(function, router.host, "-c 1", "-s 32")
        }
        val processBuilder = ProcessBuilder(*command)
//        return processBuilder
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

        //print(stringBuilder.toString()) //ONLY FOR DEBUGGING
        logging.log(stringBuilder.toString())

        if (stringBuilder.contains(Regex("time="))) {
            router.status = RouterStatus.ONLINE
        } else {
            router.status = RouterStatus.OFFLINE
        }
    }

    private fun printRoutersInfo() {
        routers.forEach { println(it) }
    }
}
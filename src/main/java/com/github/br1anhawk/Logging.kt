package com.github.br1anhawk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

class Logging {
    var isLogging = LOGGING

    private val logger = Logger.getLogger("logger")
    private val fileHandler = FileHandler(LOGGING_FILE_NAME)

    init {
        logger.addHandler(fileHandler)
        val formatter = SimpleFormatter()
        fileHandler.formatter = formatter
    }

    fun log(log: String) {
        if (isLogging) {
            logger.info(log)
            fileHandler.flush()
        }
    }
}
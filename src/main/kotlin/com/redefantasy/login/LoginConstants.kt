package com.redefantasy.login

/**
 * @author Gutyerrez
 */
object LoginConstants {

    val EMPTY_LINES = ByteArray(100)

    init {
        for (i in 0 until 100) {
            EMPTY_LINES[i] = 0x0A
        }
    }

}
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.zj.log

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by ZJJ
 */

internal val imService: ExecutorService = Executors.newFixedThreadPool(5)

fun LogCollectionUtils.ymd(time: Long = System.currentTimeMillis()): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(Date(time))
}

fun LogCollectionUtils.h(time: Long = System.currentTimeMillis()): String {
    val format = SimpleDateFormat("HH", Locale.getDefault())
    return format.format(Date(time))
}

fun LogCollectionUtils.hmsNio(time: Long = System.currentTimeMillis()): String {
    val format = SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault())
    return format.format(Date(time))
}

fun LogCollectionUtils.full(time: Long = System.currentTimeMillis()): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault())
    return format.format(Date(time))
}

fun LogCollectionUtils.dataToString(data: Any?): String {
    return DataUtils.dataToString(data)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> LogCollectionUtils.stringToMod(data: String): T? {
    return DataUtils.toModule<Any>(data) as? T
}
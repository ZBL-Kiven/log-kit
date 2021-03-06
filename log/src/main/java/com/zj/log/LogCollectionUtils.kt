@file:Suppress("MemberVisibilityCanBePrivate")

package com.zj.log

import android.app.Application
import android.content.Context
import android.util.Log
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

/**
 * Created by ZJJ
 */

@Suppress("unused")
sealed class LogCollectionUtils {

    companion object {
        const val TAG = "com.zj.LogUtils:println %s"
    }

    private var subPath: () -> String = { ymd() }
    private var fileName: () -> String = { h() }
    private var debugEnable: Boolean = false
    private var collectionAble: () -> Boolean = { false }

    abstract fun checkInit(): Boolean

    private fun init(appContext: Context?, folderName: String, subPath: () -> String, fileName: () -> String, debugEnable: Boolean, collectionAble: (() -> Boolean)?, maxRetain: Long) {
        fileUtils = FileUtils.init(appContext, folderName)
        this.subPath = subPath
        this.fileName = fileName
        this.debugEnable = debugEnable
        this.collectionAble = collectionAble ?: { false }
        if (removeAble() && maxRetain > 0) removeOldFiles(maxRetain)
    }

    private var fileUtils: FileUtils? = null

    private fun getTag(what: String?) = String.format(TAG, what)

    fun d(where: String, s: String?) {
        if (!checkInit()) return
        if (debugEnable) {
            Log.d(getTag(ErrorType.D.errorName), getLogText(where, s))
        }
    }

    fun w(where: String, s: String?) {
        if (!checkInit()) return
        if (debugEnable) {
            Log.w(getTag(ErrorType.W.errorName), getLogText(where, s))
        }
    }

    fun e(where: String, s: String?) {
        if (!checkInit()) return
        if (debugEnable) {
            Log.e(getTag(ErrorType.E.errorName), getLogText(where, s))
        }
    }

    fun writeToFile(where: String, s: String?, append: Boolean) {
        if (!checkInit()) return
        val type = ErrorType.D
        val txt = getLogText(where, s)
        if (debugEnable) {
            Log.d(getTag(type.errorName), txt)
        }
        if (collectionAble()) {
            onLogCollection(type, txt, append)
        }
    }

    fun writeToFile(what: String?, append: Boolean = true) {
        fileUtils?.save(subPath(), fileName(), what ?: "", append)
    }

    fun getLogFile(): File? {
        return fileUtils?.getFile(subPath(), fileName())
    }

    fun getCollectionHomeFolder(): File? {
        return fileUtils?.getHomePathFile()
    }

    fun getLogFileText(): String? {
        return fileUtils?.getTxt(fileUtils?.getFile(subPath(), fileName()))
    }

    private fun getLogText(where: String, s: String?): String {
        return "\n from : $where:\n case:$s\n"
    }

    private fun onLogCollection(type: ErrorType, log: String?, append: Boolean = true) {
        fileUtils?.save(subPath(), fileName(), " \n type:${type.errorName} : on  ${hmsNio()}:$log ", append)
    }

    open fun removeAble(): Boolean {
        return true
    }

    private fun removeOldFiles(maxRetain: Long) {
        try {
            fileUtils?.getHomePathFile()?.let { file ->
                if (!file.isDirectory) return
                val paths = arrayListOf<String>()
                file.listFiles()?.forEach {
                    if (System.currentTimeMillis() - it.lastModified() > maxRetain) {
                        paths.add(file.path)
                    }
                }
                paths.forEach { fileUtils?.deleteFolder(it) }
            }
        } catch (e: Exception) {
            e("remove5DaysAgoLogFiles", "error case : ${e.message}")
        }
    }

    private enum class ErrorType(internal var errorName: String?) {
        E("ERROR"), D("DEBUG"), W("WARMING")
    }

    abstract class Config : LogCollectionUtils() {
        abstract val subPath: () -> String
        abstract val fileName: () -> String
        private var isInit = false
        private var initializing = false

        final override fun checkInit(): Boolean {
            return isInit
        }

        private fun selfInit(appContext: Context?, debugEnable: Boolean, collectionAble: () -> Boolean, logsMaxRetain: Long) {
            if (isInit) return
            if (initializing) return
            initializing = true
            initConfig(appContext, ".panel", debugEnable, collectionAble, logsMaxRetain)
        }

        /**
         * must call init() before use
         * */
        fun init(appContext: Context?, folderName: String, debugEnable: Boolean, collectionAble: () -> Boolean, logsMaxRetain: Long) {
            synchronized(this) {
                isInit = false
                initializing = true
                logUtils.selfInit(appContext, debugEnable, collectionAble, logsMaxRetain)
                initConfig(appContext, folderName, debugEnable, collectionAble, logsMaxRetain)
            }
        }

        private fun getOverridePath(s: String) = overriddenFolderName(s)

        open fun overriddenFolderName(folderName: String): String {
            return folderName
        }

        open fun prepare() {}

        private fun initConfig(appContext: Context?, folderName: String, debugEnable: Boolean, collectionAble: () -> Boolean, logsMaxRetain: Long) {
            if ((appContext as? Application) == null) throw  IllegalArgumentException("please use an application context to init the log utils")
            if (collectionAble.invoke() && folderName.isEmpty()) {
                throw NullPointerException("must set a log path with open the log collectors!")
            }
            super.init(appContext, folderName, subPath, fileName, debugEnable, collectionAble, logsMaxRetain)
            prepare()
            isInit = true
            initializing = false
        }
    }
}
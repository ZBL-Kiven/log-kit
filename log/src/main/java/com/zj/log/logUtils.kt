@file:Suppress("unused")

package com.zj.log

/**
 * Created by ZJJ
 *
 * log collection utils for self
 *
 * collectionAble = auto
 * */
internal val logUtils = object : LogCollectionUtils.Config() {

    override fun overriddenFolderName(folderName: String): String {
        return "$folderName/_log_panel"
    }

    override val subPath: () -> String
        get() = { ymd() }
    override val fileName: () -> String
        get() = { h() }
}
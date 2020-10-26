@file:Suppress("unused")

package com.zj.log

/**
 * Created by ZJJ
 *
 * log collection utils for self
 *
 * collectionAble = auto
 * */
internal var logUtils = object : LogCollectionUtils.Config() {
    override val subPath: () -> String
        get() = { ymd() }
    override val fileName: () -> String
        get() = { h() }
}
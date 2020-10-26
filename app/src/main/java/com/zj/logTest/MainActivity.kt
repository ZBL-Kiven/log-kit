package com.zj.logTest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zj.log.LogCollectionUtils
import com.zj.log.ymd

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        log.init(applicationContext, "test", true, { true }, 0)
        log.printInFile("aaa", "  aaaaaaaa", true)
        log.printInFile("bbb", "  bbbbbbbb", true)
        log.printInFile("ccc", "  cccccccc", true)

    }


    val log = object : LogCollectionUtils.Config() {
        override fun overriddenFolderName(folderName: String): String {
            return "$folderName/push-logs"
        }

        override val subPath: () -> String
            get() = { ymd() }
        override val fileName: () -> String
            get() = { "logs" }

    }
}

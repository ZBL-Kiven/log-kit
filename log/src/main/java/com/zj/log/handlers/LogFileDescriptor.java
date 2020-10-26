package com.zj.log.handlers;

import java.io.File;

public class LogFileDescriptor {

    public long fileLength = 0L;

    public long lastModify = 0L;

    /**
     * it was valid in os 7.0 and upper, else it returns {@link  File#lastModified()}
     */
    public long createTime = 0L;

    public String desc = "";

    public boolean isFolder = false;

}

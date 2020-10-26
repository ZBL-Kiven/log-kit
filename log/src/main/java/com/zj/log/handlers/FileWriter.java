package com.zj.log.handlers;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

public class FileWriter {

    public static boolean write(String path, byte[] data) {
        FileChannel channel = null;
        FileLock lock = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            channel = raf.getChannel();
            lock = channel.tryLock();
            if (lock == null) return false;
            raf.seek(raf.length());
            ByteBuffer recordBuffer = ByteBuffer.wrap(data);
            channel.write(recordBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Nullable
    public static LogFileDescriptor getFileDescription(String path) {
        try {
            LogFileDescriptor descriptor = new LogFileDescriptor();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                BasicFileAttributes attr = Files.getFileAttributeView(Paths.get(path), BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).readAttributes();
                descriptor.fileLength = attr.size();
                descriptor.isFolder = attr.isDirectory();
                descriptor.lastModify = attr.lastModifiedTime().toMillis();
                descriptor.createTime = attr.creationTime().toMillis();
            } else {
                File f = new File(path);
                if (f.isDirectory() || !f.exists()) return null;
                descriptor.fileLength = f.length();
                descriptor.isFolder = f.isDirectory();
                descriptor.lastModify = f.lastModified();
                descriptor.createTime = f.lastModified();
            }
            return descriptor;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

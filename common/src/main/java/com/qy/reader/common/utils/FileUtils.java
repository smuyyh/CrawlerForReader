package com.qy.reader.common.utils;

import android.os.Environment;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created by yuyuhang on 2018/1/11.
 */
public class FileUtils {

    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String createBookRootPath() {
        String bookPath = "";
        if (isSdCardAvailable()) {
            bookPath = Environment.getExternalStorageDirectory().getPath() + "/novel";
            createDir(bookPath);
        }
        return bookPath;
    }

    /**
     * 递归创建文件夹
     *
     * @param dirPath
     * @return 创建失败返回""
     */
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                if (!file.exists()) {
                    LogUtils.i("----- 创建文件夹" + file.getAbsolutePath());
                    file.mkdir();
                } else {
                    LogUtils.i("----- 文件夹已存在" + file.getAbsolutePath());
                }
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                LogUtils.i("----- 创建文件夹" + file.getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * 递归创建文件夹
     *
     * @param file
     * @return 创建失败返回""
     */
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                LogUtils.i("----- 创建文件" + file.getAbsolutePath());
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
                LogUtils.i("----- 创建文件" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取应用储存文件.
     * @param fileName
     * @return
     */
    public static File getAppStorageFile(String fileName) {
        String path = FileUtils.createBookRootPath() + File.separator
                + fileName;
        File file = new File(path);
        if (!file.exists()) {
            FileUtils.createFile(file);
        }
        return file;
    }
}

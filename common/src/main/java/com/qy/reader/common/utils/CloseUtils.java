package com.qy.reader.common.utils;

import java.io.Closeable;
import java.io.IOException;

public final class CloseUtils {

    private CloseUtils() {
        throw new UnsupportedOperationException("");
    }

    /**
     * 关闭 IO
     *
     * @param closeables closeables
     */
    public static void closeIO(final Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
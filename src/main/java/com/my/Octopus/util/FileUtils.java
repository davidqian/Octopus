package com.my.Octopus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    /**
     * 返回文件内容字符串
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String getStringFromFile(String filePath) throws IOException {
        File f = new File(filePath);

        // 文件不存在
        if (!f.exists()) {
            return null;
        }

        FileReader r = new FileReader(filePath);
        BufferedReader b = new BufferedReader(r);
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = b.readLine()) != null) {
            sb.append(s.trim());
        }

        r.close();
        return sb.toString();
    }

    public static void writeStringToFile(String str, String filePath) throws IOException {
        // 上级目录不存在，则创建之
        String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
        mkdirRecursive(dirPath);

        FileWriter writer = new FileWriter(filePath);
        writer.write(str);
        writer.close();
    }

    public static void mkdirRecursive(String dirPath) {
        File file = new File(dirPath);
        file.mkdirs();
    }

    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }
}

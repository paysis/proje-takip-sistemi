package com.gurkan.utils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class FileOperations {
    public static ArrayList<File> getFilesInDir(String dirPath) {
        ArrayList<File> rets = new ArrayList<>();

        File dir = new File(dirPath);

        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                rets.add(files[i]);
            }
        }

        return rets;
    }

    // inspired from
    // https://stackoverflow.com/questions/19414453/how-to-get-resources-directory-path-programmatically
    public static File getFileFromResource(String resourcePath, Class classLoader) {
        URL resourceUrl = classLoader.getResource(resourcePath);
        File ret = null;

        try {
            ret = new File(resourceUrl.toURI());
        } catch (URISyntaxException ex) {
            ret = new File(resourceUrl.getPath());
        }

        // ret = new File(resourceUrl.getFile());
        return ret;
    }
}

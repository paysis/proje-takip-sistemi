package com.gurkan.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RecentDbManager {
    public static String recently_opened_filepath = "recently_opened.txt";

    public static void insertRecentlyOpened(String databaseFilePath) {
        try {
            File dbFile = new File(recently_opened_filepath);

            if (!dbFile.exists()) {
                try {
                    dbFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ArrayList<String> entryList = new ArrayList<>();

            entryList.add(databaseFilePath.trim());

            BufferedReader br = new BufferedReader(new FileReader(recently_opened_filepath));
            String currentLine;

            while ((currentLine = br.readLine()) != null && entryList.size() < 10) {
                if (!entryList.contains(currentLine.trim())) {

                    entryList.add(currentLine.trim());
                }
            }

            br.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(recently_opened_filepath, false));

            for (String row : entryList) {
                bw.write(row.endsWith("\n") ? row : row + "\n");
            }

            bw.close();
        } catch (IOException ioe) {
            throw new RuntimeException("[RecentDbManager] insertRecentlyOpened error: " + ioe.getMessage());
        }
    }

    public static String[] getRecentlyOpened() {
        try {
            File dbFile = new File(recently_opened_filepath);

            if (!dbFile.exists()) {
                try {
                    dbFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ArrayList<String> entryList = new ArrayList<>();

            BufferedReader br = new BufferedReader(new FileReader(recently_opened_filepath));
            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                entryList.add(currentLine.trim());
            }

            br.close();

            return entryList.toArray(new String[0]);
        } catch (IOException ioe) {
            throw new RuntimeException("[RecentDbManager] insertRecentlyOpened error: " + ioe.getMessage());
        }
    }
}

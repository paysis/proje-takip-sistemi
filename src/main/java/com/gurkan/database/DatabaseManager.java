package com.gurkan.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gurkan.data.Note;
import com.gurkan.data.Project;
import com.gurkan.utils.ArrayOperations;
import com.gurkan.utils.FileOperations;

import javafx.scene.control.TreeItem;

public class DatabaseManager {
    public static String database_filepath = "";
    public static String database_objectdirpath = "objects/";

    public static String getDatabaseFilePath() {
        return database_filepath;
    }

    public static void setDatabaseFilePath(String databaseFilePath) {
        DatabaseManager.database_filepath = databaseFilePath;

        File dbFile = new File(databaseFilePath);

        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Extracts all features of all data available on the database.
     * 
     * @return
     * @throws IOException
     */
    public static ArrayList<Map<String, String>> extractAllFeatures() throws IOException {
        final ArrayList<Map<String, String>> rets = new ArrayList<>();

        final ArrayList<String> rawRows = readRawRows();

        for (String row : rawRows) {
            Map<String, String> extractedFeatures = extractFeatures(row);

            if (extractedFeatures != null)
                rets.add(extractedFeatures);
        }

        return rets;
    }

    /**
     * Extracts only the given row data.
     * 
     * @param row
     * @return
     */
    public static Map<String, String> extractFeatures(String row) {
        if (row.length() < 2) {
            return null;
        }

        final Map<String, String> features = new HashMap<>();

        String[] rawFeatures = row.contains("|") ? row.split("|") : new String[] { row };

        for (int i = 0; i < rawFeatures.length; i++) {
            String[] rawFeature = rawFeatures[i].split(":");

            String featureKey = rawFeature[0];
            String featureValue = rawFeature[1].substring(rawFeature[1].indexOf('"') + 1,
                    rawFeature[1].lastIndexOf('"'));

            features.put(featureKey, featureValue);
        }

        return features;
    }

    /**
     * Reads all the data available on the database and returns it as an array.
     * 
     * @return
     * @throws IOException
     */
    public static ArrayList<String> readRawRows() throws IOException {
        // int currentRowCount = getRowCount();

        ArrayList<String> dataTable = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(database_filepath));
        String currentLine;

        while ((currentLine = br.readLine()) != null) {
            dataTable.add(currentLine);
        }

        br.close();

        return dataTable;
    }

    /**
     * Should be used with main database file operations.
     * 
     * @param <T>
     * @param objName
     * @return
     */
    public static <T extends Project> T retrieveProject(String objName) {
        T ret = null;

        try {
            FileInputStream fileIn = new FileInputStream(database_objectdirpath + objName + ".ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            ret = (T) in.readObject();
            ret.setRootTreeItem((TreeItem<Note>) in.readObject());
            in.close();
            fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }

        return ret;
    }

    /**
     * Can be used with Factory to create all stored objects in one go.
     * 
     * @param <T>
     * @return
     */
    public static <T extends Project> ArrayList<T> retrieveAllProjects() {
        ArrayList<T> objArr = new ArrayList<>();

        ArrayList<File> objectFiles = FileOperations.getFilesInDir(database_objectdirpath);

        for (File curFile : objectFiles) {

            String curFileName = curFile.getName().substring(0, curFile.getName().lastIndexOf('.'));

            T obj = retrieveProject(curFileName);

            if (obj != null) {
                objArr.add(obj);
            }
        }

        return objArr;
    }

    public static <T extends Project> ArrayList<T> retrieveProjects(ArrayList<String> objectListToRetrieve) {
        ArrayList<T> objArr = new ArrayList<>();

        ArrayList<File> objectFiles = FileOperations.getFilesInDir(database_objectdirpath);

        for (String objectName : objectListToRetrieve) {

            if (ArrayOperations.<File>any(objectFiles, (element, index) -> {

                String curFileName = element.getName().substring(0, element.getName().lastIndexOf('.'));

                if (curFileName.equals(objectName)) {
                    return true;
                }

                return false;
            })) {

                objArr.add(retrieveProject(objectName));

            }

        }

        return objArr;
    }

    public static <T extends Project> void insertProject(T object) {
        try {
            File objectDir = new File(database_objectdirpath);
            if (!objectDir.exists()) {
                objectDir.mkdirs();
            }

            File objectFile = new File(database_objectdirpath + object.getFileName() + ".ser");
            if (!objectFile.exists()) {
                try {
                    objectFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            FileOutputStream fileOut = new FileOutputStream(objectFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.writeObject(new TreeItemSerializationWrapper<Note>(object.getRootTreeItem()));
            out.close();
            fileOut.close();

            ArrayList<String> rows = readRawRows();

            String objRenderStr = object.render();

            boolean isExisting = false;
            for (String row : rows) {
                if (row.equals(objRenderStr)) {
                    isExisting = true;
                }
            }

            if (!isExisting) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(database_filepath, true));

                bw.write(objRenderStr);

                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Project> void saveProjects(T[] objArr) {
        clearDatabase();

        for (T obj : objArr) {
            insertProject(obj);
        }
    }

    public static void clearDatabase() {
        try {
            (new FileWriter(database_filepath, false)).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

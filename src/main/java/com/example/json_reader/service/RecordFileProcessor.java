package com.example.json_reader.service;

import com.example.json_reader.Constant;
import com.example.json_reader.model.State;

import java.io.*;

public class RecordFileProcessor {

    private final String fileFolder;
    private String filename;
    private final String fileFullName;
    private final String newFolder;

    private int recordCount = 0;
    private int fileNumber = 1;

    public RecordFileProcessor(File file) {
        fileFolder = file.getParent();
        newFolder = fileFolder + "\\data";
        File data = new File(newFolder);

        if (!data.exists()) {
            data.mkdir();
        }

        fileFullName = file.getPath();
        filename = file.getName();
        int pos = filename.lastIndexOf(".");

        if (pos > 0) {
            filename = filename.substring(0, pos);
        }
    }

    private boolean isBaseFile(String fileName) {
        for(int i = 0; i < this.filename.length(); i++) {
            if (fileName.charAt(i) != this.filename.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public  static boolean compareFiles(String f1, String f2) {
        int n = Math.min(f1.length(), f2.length());
        for(int i = 0; i < n; i++) {
            if(f1.charAt(i) != f2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean isFileProcessed() {
        File folder = new File(newFolder);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && isBaseFile(file.getName())){
                    return true;
                }
            }
        }
        return false;
    }

    public void processRecords() throws IOException {

        BufferedReader reader = null;
        FileWriter writer = null;

        try {
            State state = StateService.getState();

            recordCount = state.getLastProcessedLine();
            fileNumber = state.getLastFileCount();

            reader = new BufferedReader(new FileReader(fileFullName));
            String line;
            line = reader.readLine().split(":")[1];

            for (int i = 0; i < fileNumber * 100 + recordCount; i++) {
                line = reader.readLine();
            }

            writer = new FileWriter(getNewFilename());

            while (line != null) {
                line = reader.readLine();

                if(line == null) {
                    break;
                }
                recordCount++;

                if (recordCount == Constant.FILE_SIZE) {
                    writer.close();
                    fileNumber++;
                    writer = new FileWriter(getNewFilename());
                    recordCount = 0;
                }
                writer.write(line + Constant.LINE_SEPARATOR);

                StateService.updateState(recordCount, fileNumber, filename);
            }
            StateService.deleteState();
        } finally {
            if (reader != null) {
                reader.close();
            }

            if (writer != null) {
                writer.close();
            }
        }
    }

    private String getNewFilename() {
        String fileSuffix = String.format("%04d", fileNumber);
        return newFolder + "\\" + filename + "-" + fileSuffix + ".log";
    }


}

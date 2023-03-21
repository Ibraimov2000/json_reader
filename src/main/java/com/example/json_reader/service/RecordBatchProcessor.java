package com.example.json_reader.service;

import com.example.json_reader.model.State;

import java.io.*;

public class RecordBatchProcessor {
    private static final int BATCH_SIZE = 100;
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final String fileFolder;
    private String fileName;
    private final String fileFullName;
    private final String dataFolder;

    private int recordCount = 0;
    private int batchNumber = 1;

    public RecordBatchProcessor(File file) {
        fileFolder = file.getParent();
        dataFolder = fileFolder + "\\data";
        File data = new File(dataFolder);
        if (!data.exists())
            data.mkdir();
        fileFullName = file.getPath();
        fileName = file.getName();
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }
    }

    private boolean isBaseFile(String fileName){
        for(int i = 0; i < this.fileName.length(); i++)
            if(fileName.charAt(i) != this.fileName.charAt(i))
                return false;

        return true;
    }

    public  static boolean compareFiles(String f1, String f2){
        int n = Math.min(f1.length(), f2.length());
        for(int i = 0; i < n; i++)
            if(f1.charAt(i) != f2.charAt(i)) return false;
        return true;
    }

    public boolean isFileProcessed(String pathToFolder) {
        File folder = new File(pathToFolder);
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
            batchNumber = state.getLastFileCount();

            reader = new BufferedReader(new FileReader(fileFullName));
            String line;
            line = reader.readLine().split(":")[1];

            for (int i = 0; i < batchNumber * 100 + recordCount; i++) {
                line = reader.readLine();
            }

            writer = new FileWriter(getBatchFilename());

            while (line != null){
                line = reader.readLine();
                if(line == null) break;
                recordCount++;
                if (recordCount == BATCH_SIZE) {
                    writer.close();
                    batchNumber++;
                    writer = new FileWriter(getBatchFilename());
                    recordCount = 0;
                }
                writer.write(line + LINE_SEPARATOR);
                writer.flush();

                state.setLastProcessedLine(recordCount);
                state.setLastFileCount(batchNumber);
                state.setFilenameToRead(fileName);

                StateService.saveState(state);
            }
            StateService.delete();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

    private String getBatchFilename() {
        String batchSuffix = String.format("%04d", batchNumber);
        return dataFolder + "\\" + fileName + "-" + batchSuffix + ".log";
    }
}

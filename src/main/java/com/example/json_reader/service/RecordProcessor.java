package com.example.json_reader.service;

import com.example.json_reader.Constant;
import com.example.json_reader.model.State;

import java.io.*;

public class RecordProcessor {

    private String filename;
    private String fileFullName;
    private String newDirectory;
    private final File configFile;


    public RecordProcessor(File file) throws IOException {
        filename = getFilename(file);
        createNewDir(file);
        configFile = createConfigFile();
    }

    private String getFilename(File file) {
        fileFullName = file.getPath();
        filename = file.getName();
        int pos = filename.lastIndexOf(Constant.POINT);

        if (pos > 0) {
            return filename.substring(0, pos);
        } else {
            return filename;
        }
    }

    private String getNewFilename(int fileNumber) {
        String fileSuffix = String.format("%04d", fileNumber);
        return newDirectory + "\\" + filename + "-" + fileSuffix + Constant.EXTENSION;
    }

    private void createNewDir(File file) {
        String fileDirectory = file.getParent();
        newDirectory = fileDirectory + Constant.DIRECTORY;
        File data = new File(newDirectory);

        if (!data.exists()) {
            data.mkdir();
        }
    }

    private File createConfigFile() throws IOException {
        File file = new File(Constant.CONFIG_FILE);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
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
        File folder = new File(newDirectory);
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
        BufferedWriter writer = null;

        try {
            State state = StateService.getState(configFile);

            int recordCount = state.getLastProcessedLine();
            int fileNumber = state.getLastFileCount();

            reader = new BufferedReader(new FileReader(fileFullName));
            String line = reader.readLine();

            for (int i = 0; i < fileNumber * 100 + recordCount; i++) {
                line = reader.readLine();
            }

            writer = new BufferedWriter(new FileWriter(getNewFilename(fileNumber), true));

            while (line != null) {
                line = reader.readLine();

                if(line == null) {
                    break;
                }
                recordCount++;

                int batchSize = Constant.BATCH_SIZE - 1;

                if (fileNumber == 0) {
                     batchSize = Constant.BATCH_SIZE;
                }

                if (recordCount == batchSize) {
                    writer.close();
                    fileNumber++;
                    writer = new BufferedWriter(new FileWriter(getNewFilename(fileNumber)));
                    recordCount = 0;
                }
                writer.write(line);
                writer.newLine();

                StateService.updateState(configFile, recordCount, fileNumber, filename);
            }
            StateService.deleteState(configFile);
        } finally {
            if (reader != null) {
                reader.close();
            }

            if (writer != null) {
                writer.close();
            }
        }
    }
}

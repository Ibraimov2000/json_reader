package com.example.json_reader.service;

import com.example.json_reader.model.State;

import java.io.*;

public class StateService {

    public static State getState(File file) throws IOException {

        State state = new State();

        if (file.exists()) {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                if (fileReader.ready()) {
                    String[] line = fileReader.readLine().split(" ");
                    state.setFilenameToRead(line[0].trim());
                    state.setLastProcessedLine(Integer.parseInt(line[1].trim()));
                    state.setLastFileCount(Integer.parseInt(line[2].trim()));
                    return state;
                }
            }
        }
        return state;
    }

    public static void updateState(File file, int recordCount, int fileNumber, String filename) throws IOException {
        State state = new State();
        state.setLastProcessedLine(recordCount);
        state.setLastFileCount(fileNumber);
        state.setFilenameToRead(filename);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(state.toString());
            fileWriter.flush();
        }
    }

    public static void deleteState(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
}

package com.example.json_reader.service;

import com.example.json_reader.model.State;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
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
        log.info("IN getState, the state: {} got", state);
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
        log.info("IN updateState,the state: {} saved", state.toString());
    }

    public static void deleteState(File file) {
        if (file.exists()) {
            file.delete();
        }
        log.info("IN deleteState, file: {} removed", file);
    }
}

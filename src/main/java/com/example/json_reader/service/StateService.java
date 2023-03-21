package com.example.json_reader.service;

import com.example.json_reader.model.State;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StateService {

    private static final String CONFIG_FILE = "config.json";

    public static State getState() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            return new State();
        }
        else {
            int count = (int) Files.lines(Paths.get(file.getPath())).count();
             if (count == 0) {
                 return new State();
             }
        }
        return mapper.readValue(file, State.class);
    }

    public static void saveState(State state) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            file.createNewFile();
        }

        mapper.writeValue(file, state);
    }

    public static void delete() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}

package com.example.json_reader;

import com.example.json_reader.model.State;
import com.example.json_reader.service.RecordBatchProcessor;
import com.example.json_reader.service.StateService;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class JsonReaderApplication {

    public static void main(String[] args) throws IOException {
        String pathToFolder = "D:\\IdeaProjects\\demir";
        String pathToDataFolder ="D:\\IdeaProjects\\demir\\data";
        File folder = new File(pathToFolder);
        File[] files = folder.listFiles();

        State state = StateService.getState();

        if (files != null) {
            if(state.getFilenameToRead() != null)
            for (File file : files) {
                if(!file.isFile()) continue;
                String fileName = state.getFilenameToRead();
                if (RecordBatchProcessor.compareFiles(file.getName(), fileName)) {
                    RecordBatchProcessor processor = new RecordBatchProcessor(file);
                    processor.processRecords();
                }
            }

            for (File file: files) {
                if(!file.isFile()) continue;
                RecordBatchProcessor processor = new RecordBatchProcessor(file);
                if(processor.isFileProcessed(pathToDataFolder)) continue;
                processor.processRecords();
            }
        }
    }
}

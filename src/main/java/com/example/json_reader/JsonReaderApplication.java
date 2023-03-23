package com.example.json_reader;

import com.example.json_reader.model.State;
import com.example.json_reader.service.RecordProcessor;
import com.example.json_reader.service.StateService;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class JsonReaderApplication {

    public static void main(String[] args) throws IOException {

        File folder = new File(Constant.FOLDER_PATH);
        File[] files = folder.listFiles();

        State state = StateService.getState(new File(Constant.CONFIG_FILE));

        if (files != null) {
            if(state.getFilenameToRead() != null) {
                for (File file : files) {
                    if(!file.isFile()) {
                        continue;
                    }
                    String fileName = state.getFilenameToRead();

                    if (RecordProcessor.compareFiles(file.getName(), fileName)) {
                        RecordProcessor processor = new RecordProcessor(file);
                        processor.processRecords();
                    }
                }
            }

            for (File file: files) {
                if(!file.isFile()) {
                    continue;
                }

                RecordProcessor processor = new RecordProcessor(file);
                if(processor.isFileProcessed()) {
                    continue;
                }
                processor.processRecords();
            }
        }
    }
}

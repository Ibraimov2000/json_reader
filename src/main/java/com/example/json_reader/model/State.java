package com.example.json_reader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {

    @Override
    public String toString() {
        return filenameToRead + " " + lastProcessedLine + " " + lastFileCount;
    }

    private String filenameToRead;
    private int lastProcessedLine;
    private int lastFileCount;
}

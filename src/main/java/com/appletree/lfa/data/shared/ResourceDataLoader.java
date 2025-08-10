package com.appletree.lfa.data.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceDataLoader {

    @Qualifier("swissDateFormatObjectMapper")
    private final ObjectMapper objectMapper;

    public <T> List<T> readDataFromResources(String resourceFilePath, Class<T> clazz) {
        InputStream inputStream = clazz.getResourceAsStream(resourceFilePath);
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        try {
            return objectMapper.readValue(inputStream, listType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

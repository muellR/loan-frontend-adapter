package com.appletree.lfa.data.financingobject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FinancingObjectRepository {

    private List<FinancingObject> financingObjects;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
        TypeReference<List<FinancingObject>> typeReference = new TypeReference<>() {};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/data/20231210_TestData_FINANCING_OBJECT.json");
        financingObjects = objectMapper.readValue(inputStream, typeReference);
    }

    public List<FinancingObject> findFinancingObjectByUserId(final Long userId) {
        return financingObjects.stream()
                .filter(fo -> fo.getOwners() != null)
                .filter(fo -> fo.getOwners().stream().anyMatch(o -> o.getId().equals(userId)))
                .toList();
    }
}

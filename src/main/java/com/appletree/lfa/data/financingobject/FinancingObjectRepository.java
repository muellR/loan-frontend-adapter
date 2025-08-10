package com.appletree.lfa.data.financingobject;

import com.appletree.lfa.data.shared.ResourceDataLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FinancingObjectRepository {

    private final ResourceDataLoader resourceDataLoader;
    private List<FinancingObject> financingObjects;

    public List<FinancingObject> findByUserId(final Long userId) throws IOException {
        if (userId == null) {
            throw new IllegalArgumentException("null userId received");
        }
        if (financingObjects == null) {
            financingObjects = resourceDataLoader.readDataFromResources("/data/20231210_TestData_FINANCING_OBJECT.json", FinancingObject.class);
        }
        return financingObjects.stream()
                .filter(fo -> fo.getOwners() != null)
                .filter(fo -> fo.getOwners().stream().anyMatch(o -> o.getId().equals(userId)))
                .toList();
    }
}

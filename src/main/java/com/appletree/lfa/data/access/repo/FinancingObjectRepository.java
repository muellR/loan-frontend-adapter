package com.appletree.lfa.data.access.repo;

import com.appletree.lfa.data.access.ResourceDataLoader;
import com.appletree.lfa.data.model.financingobject.FinancingObject;
import com.appletree.lfa.data.model.financingobject.FinancingObjectOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FinancingObjectRepository {

    private final ResourceDataLoader resourceDataLoader;
    private List<FinancingObject> financingObjects;

    public List<FinancingObject> findByUserId(final Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("null userId received");
        }
        if (financingObjects == null) {
            financingObjects = getFinancingObjects();
        }
        return financingObjects.stream()
                .filter(fo -> fo.getOwners() != null)
                .filter(fo -> fo.getOwners().stream().anyMatch(o -> o.getId().equals(userId)))
                .toList();
    }

    public List<String> findUserIds() {
        if (financingObjects == null) {
            financingObjects = getFinancingObjects();
        }
        return financingObjects.stream()
                .flatMap(fo -> fo.getOwners().stream())
                .map(FinancingObjectOwner::getId)
                .map(String::valueOf)
                .toList();
    }

    public List<FinancingObject> getFinancingObjects() {
        return resourceDataLoader.readDataFromResources("/data/20231210_TestData_FINANCING_OBJECT.json", FinancingObject.class);
    }
}

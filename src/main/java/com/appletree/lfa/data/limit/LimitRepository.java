package com.appletree.lfa.data.limit;

import com.appletree.lfa.data.shared.ResourceDataLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LimitRepository {

    private final ResourceDataLoader resourceDataLoader;
    private List<Limit> limits;

    public Limit findById(final Long id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("null id received");
        }
        if (limits == null) {
            limits = resourceDataLoader.readDataFromResources("/data/20231214_TestData_LIMITS.json", Limit.class);
        }
        return limits.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }
}

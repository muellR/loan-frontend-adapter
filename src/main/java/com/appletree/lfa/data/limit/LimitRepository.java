package com.appletree.lfa.data.limit;

import com.appletree.lfa.data.shared.ResourceDataLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class LimitRepository {

    private final ResourceDataLoader resourceDataLoader;
    private List<Limit> limits;

    public Map<Long, Limit> findByIds(final List<Long> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("null ids received");
        }
        if (limits == null) {
            limits = getLimits();
        }
        return limits.stream()
                .filter(l -> ids.contains(l.getId()))
                .collect(toMap(Limit::getId, l -> l));
    }

    public List<Limit> getLimits() {
        return resourceDataLoader.readDataFromResources("/data/20231214_TestData_LIMITS.json", Limit.class);
    }
}

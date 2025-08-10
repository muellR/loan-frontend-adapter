package com.appletree.lfa.data.product;

import com.appletree.lfa.data.shared.ResourceDataLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final ResourceDataLoader resourceDataLoader;
    private List<Product> products;

    public List<Product> findByIds(final List<Long> ids) throws IOException {
        if(ids == null) {
            throw new IllegalArgumentException("null ids received");
        }
        if (products == null) {
            products = resourceDataLoader.readDataFromResources("/data/20231214_TestData_PRODUCTS.json", Product.class);
        }
        return products.stream()
                .filter(l -> ids.contains(l.getId()))
                .toList();
    }
}

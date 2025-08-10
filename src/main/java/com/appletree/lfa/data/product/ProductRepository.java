package com.appletree.lfa.data.product;

import com.appletree.lfa.data.shared.ResourceDataLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final ResourceDataLoader resourceDataLoader;
    private List<Product> products;

    public Map<Long, Product> findByIds(final List<Long> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("null ids received");
        }
        if (products == null) {
            products = getProducts();
        }
        return products.stream()
                .filter(p -> ids.contains(p.getId()))
                .collect(toMap(Product::getId, p -> p));
    }

    public List<Product> getProducts() {
        return resourceDataLoader.readDataFromResources("/data/20231214_TestData_PRODUCTS.json", Product.class);
    }
}

package com.example.carsharingapp.repository.car.spec;

import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class BrandSpecificationProvider implements SpecificationProvider<Car> {
    public static final String BRAND_KEY = "brand";

    @Override
    public String getKey() {
        return BRAND_KEY;
    }

    @Override
    public Specification<Car> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder) -> root.get(BRAND_KEY)
                .in(Arrays.stream(params).toArray()));
    }
}

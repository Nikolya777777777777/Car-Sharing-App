package com.example.carsharingapp.repository.car.spec;

import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class ModelSpecificationProvider implements SpecificationProvider<Car> {
    public static final String MODEL_KEY = "model";

    @Override
    public String getKey() {
        return MODEL_KEY;
    }

    @Override
    public Specification<Car> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder) -> root.get(MODEL_KEY)
                .in(Arrays.stream(params).toArray()));
    }
}

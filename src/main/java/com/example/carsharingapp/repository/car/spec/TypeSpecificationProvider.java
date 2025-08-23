package com.example.carsharingapp.repository.car.spec;

import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class TypeSpecificationProvider implements SpecificationProvider<Car> {
    public static final String TYPE_KEY = "type";

    @Override
    public String getKey() {
        return TYPE_KEY;
    }

    @Override
    public Specification<Car> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder) -> root.get(TYPE_KEY)
                .in(Arrays.stream(params).toArray()));
    }
}

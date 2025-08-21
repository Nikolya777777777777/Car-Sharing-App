package com.example.carsharingapp.repository.car.spec;

import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class DailyFeeSpecificationProvider implements SpecificationProvider<Car> {
    public static final String DAILY_FEE_KEY = "daily_fee";

    @Override
    public String getKey() {
        return DAILY_FEE_KEY;
    }

    @Override
    public Specification<Car> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder) -> root.get(DAILY_FEE_KEY)
                .in(Arrays.stream(params).toArray()));
    }
}

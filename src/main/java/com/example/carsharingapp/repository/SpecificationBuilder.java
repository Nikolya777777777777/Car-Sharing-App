package com.example.carsharingapp.repository;

import com.example.carsharingapp.dto.car.CarSearchParamsDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(CarSearchParamsDto searchParameters);
}

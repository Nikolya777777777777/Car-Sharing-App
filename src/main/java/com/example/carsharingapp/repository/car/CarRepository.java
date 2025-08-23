package com.example.carsharingapp.repository.car;

import com.example.carsharingapp.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car,Long> {
    Page<Car> findAll(Specification<Car> carSpecification, Pageable pageable);
}

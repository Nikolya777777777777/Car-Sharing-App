package com.example.carsharingapp.repository.car;

import com.example.carsharingapp.dto.car.CarSearchParamsDto;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.repository.SpecificationBuilder;
import com.example.carsharingapp.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarSpecificationBuilder implements SpecificationBuilder<Car> {
    private final SpecificationProviderManager<Car> carSpecificationProviderManager;

    @Override
    public Specification<Car> build(CarSearchParamsDto searchParameters) {
        Specification<Car> spec = Specification.where(null);
        if (searchParameters.models() != null && searchParameters.models().length > 0) {
            spec = spec.and(carSpecificationProviderManager
                    .getSpecificationProvider(MODELS_KEY)
                    .getSpecification(searchParameters.authors()));
        }
        if (searchParameters.isbns() != null && searchParameters.isbns().length > 0) {
            spec = spec.and(carSpecificationProviderManager
                    .getSpecificationProvider(ISBN_KEY)
                    .getSpecification(searchParameters.isbns()));
        }
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            spec = spec.and(carSpecificationProviderManager
                    .getSpecificationProvider(TITLE_KEY)
                    .getSpecification(searchParameters.titles()));
        }
        return spec;
    }
}

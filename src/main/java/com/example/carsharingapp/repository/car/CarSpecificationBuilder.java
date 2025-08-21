package com.example.carsharingapp.repository.car;

import static com.example.carsharingapp.repository.car.spec.BrandSpecificationProvider.BRAND_KEY;
import static com.example.carsharingapp.repository.car.spec.DailyFeeSpecificationProvider.DAILY_FEE_KEY;
import static com.example.carsharingapp.repository.car.spec.ModelSpecificationProvider.MODEL_KEY;
import static com.example.carsharingapp.repository.car.spec.TypeSpecificationProvider.TYPE_KEY;

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
        Specification<Car> spec = null;
        if (searchParameters.models() != null && searchParameters.models().length > 0) {
            Specification<Car> modelSpec = carSpecificationProviderManager
                    .getSpecificationProvider(MODEL_KEY)
                    .getSpecification(searchParameters.models());
            spec = spec == null ? modelSpec : spec.and(modelSpec);
        }
        if (searchParameters.brands() != null && searchParameters.brands().length > 0) {
            Specification<Car> brandSpec = carSpecificationProviderManager
                    .getSpecificationProvider(BRAND_KEY)
                    .getSpecification(searchParameters.brands());
            spec = spec == null ? brandSpec : spec.and(brandSpec);
        }
        if (searchParameters.type() != null) {
            Specification<Car> typeSpecification = carSpecificationProviderManager
                    .getSpecificationProvider(TYPE_KEY)
                    .getSpecification(new String[]{String.valueOf(searchParameters.type())});
            spec = spec == null ? typeSpecification : spec.and(typeSpecification);

        }

        if (searchParameters.dailyFee() != null) {
            Specification<Car> dailyFeeSpecification = carSpecificationProviderManager
                    .getSpecificationProvider(DAILY_FEE_KEY)
                    .getSpecification(new String[]{String.valueOf(searchParameters.dailyFee())});
            spec = spec == null ? dailyFeeSpecification : spec.and(dailyFeeSpecification);
        }
        return spec;
    }
}

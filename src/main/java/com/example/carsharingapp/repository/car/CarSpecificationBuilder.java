package com.example.carsharingapp.repository.car;

import static com.example.carsharingapp.repository.car.spec.BrandSpecificationProvider.BRAND_KEY;
import static com.example.carsharingapp.repository.car.spec.DailyFeeSpecificationProvider.DAILY_FEE_KEY;
import static com.example.carsharingapp.repository.car.spec.ModelSpecificationProvider.MODEL_KEY;
import static com.example.carsharingapp.repository.car.spec.TypeSpecificationProvider.TYPE_KEY;

import com.example.carsharingapp.dto.car.CarSearchParamsDto;
import com.example.carsharingapp.model.car.Car;
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
        if (searchParameters.getModels() != null && searchParameters.getModels().length > 0) {
            Specification<Car> modelSpec = carSpecificationProviderManager
                    .getSpecificationProvider(MODEL_KEY)
                    .getSpecification(searchParameters.getModels());
            spec = spec == null ? modelSpec : spec.and(modelSpec);
        }
        if (searchParameters.getBrands() != null && searchParameters.getBrands().length > 0) {
            Specification<Car> brandSpec = carSpecificationProviderManager
                    .getSpecificationProvider(BRAND_KEY)
                    .getSpecification(searchParameters.getBrands());
            spec = spec == null ? brandSpec : spec.and(brandSpec);
        }
        if (searchParameters.getType() != null) {
            Specification<Car> typeSpecification = carSpecificationProviderManager
                    .getSpecificationProvider(TYPE_KEY)
                    .getSpecification(new String[]{String.valueOf(searchParameters.getType())});
            spec = spec == null ? typeSpecification : spec.and(typeSpecification);

        }

        if (searchParameters.getDailyFee() != null) {
            Specification<Car> dailyFeeSpecification = carSpecificationProviderManager
                    .getSpecificationProvider(DAILY_FEE_KEY)
                    .getSpecification(new String[]{String.valueOf(searchParameters.getDailyFee())});
            spec = spec == null ? dailyFeeSpecification : spec.and(dailyFeeSpecification);
        }
        return spec;
    }
}

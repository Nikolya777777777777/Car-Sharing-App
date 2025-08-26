package com.example.carsharingapp.repository.car;

import com.example.carsharingapp.exception.SpecificationProviderManagerException;
import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.repository.SpecificationProvider;
import com.example.carsharingapp.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CarSpecificationProviderManager implements SpecificationProviderManager<Car> {
    private final List<SpecificationProvider<Car>> carSpecificationProviders;

    @Override
    public SpecificationProvider<Car> getSpecificationProvider(String key) {
        return carSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationProviderManagerException("Can't find "
                        + "correct specification provider for key " + key));
    }
}

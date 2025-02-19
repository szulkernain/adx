package com.ambrygen.adx.repositories;

import com.ambrygen.adx.models.Vehicle;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VehicleRepository extends CrudRepository<Vehicle, String> {
    Optional<Vehicle> findByVinAndYear(String vin, Integer year);
}

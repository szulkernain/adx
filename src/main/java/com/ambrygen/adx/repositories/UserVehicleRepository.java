package com.ambrygen.adx.repositories;

import com.ambrygen.adx.models.UserVehicle;
import com.ambrygen.adx.models.Vehicle;
import com.ambrygen.adx.models.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserVehicleRepository extends JpaRepository<UserVehicle, String> {

    List<UserVehicle> findByUserOrderByPurchaseDateDesc(User user);
    Optional<UserVehicle> findByUserAndVehicle(User user, Vehicle vehicle);
}

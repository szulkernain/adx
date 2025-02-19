package com.ambrygen.adx.services;

import com.ambrygen.adx.models.UserVehicle;
import com.ambrygen.adx.models.VehicleServiceLog;
import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.repositories.UserVehicleRepository;
import com.ambrygen.adx.repositories.VehicleServiceLogRepository;
import com.ambrygen.adx.services.security.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserVehicleService {
    private static final Logger logger = LoggerFactory.getLogger(UserVehicleService.class);
    private final UserVehicleRepository userVehicleRepository;
    private final VehicleServiceLogRepository vehicleServiceLogRepository;
    private final UserService userService;

    public void deleteAllUserData(User user) {
        List<UserVehicle> userVehicles = userVehicleRepository.findByUserOrderByPurchaseDateDesc(user);
        for (UserVehicle userVehicle : userVehicles) {
            //Delete all service logs for vehicle
            List<VehicleServiceLog> vehicleServiceLogs =
                    vehicleServiceLogRepository.findByUserVehicleOrderByServiceDateDesc(userVehicle);
            for (VehicleServiceLog serviceLog : vehicleServiceLogs) {
                vehicleServiceLogRepository.delete(serviceLog);
            }
            //Delete all user's vehicles
            userVehicleRepository.delete(userVehicle);
        }
        //Delete the user!
        userService.delete(UUID.fromString(user.getId()));
    }
}

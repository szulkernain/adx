package com.ambrygen.adx.services;

import com.ambrygen.adx.dto.ServiceLog;
import com.ambrygen.adx.dto.VehicleServiceLogRequest;
import com.ambrygen.adx.models.*;
import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.repositories.UserVehicleRepository;
import com.ambrygen.adx.repositories.VehicleServiceLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ServiceLogService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogService.class);

    private final UserVehicleRepository userVehicleRepository;

    private final VehicleServiceLogRepository vehicleServiceLogRepository;

    public Optional<VehicleServiceLog> findById(String vehicleServiceLogId) {
        return vehicleServiceLogRepository.findById(vehicleServiceLogId);
    }

    public Integer getMinServiceYear(String userId) {
        return vehicleServiceLogRepository.getMinServiceYear(userId);
    }

    public List<ExpenseByServiceDate> getExpenseByServiceDateForUser(String userId) {
        return
                vehicleServiceLogRepository.getExpenseByServiceDate(userId);
    }

    public List<ExpenseByServiceDate> getExpenseByServiceDateForUserAndVehicle(String userId, String vehicleId) {
        return
                vehicleServiceLogRepository.getExpenseByServiceDateForVehicle(userId,vehicleId);
    }

    public List<ExpenseByServiceTypeAndServiceDate> getExpenseByServiceTypeAndServiceDateForUser(String userId) {
        return
                vehicleServiceLogRepository.getExpenseByServiceTypeAndServiceDate(userId);
    }

    public List<ExpenseByServiceTypeAndServiceDate> getExpenseByServiceTypeAndServiceDateForUserAndVehicle(String userId,String vehicleId) {
        return
                vehicleServiceLogRepository.getExpenseByServiceTypeAndServiceDateForVehicle(userId,vehicleId);
    }

    public List<ExpenseByVehicleAndServiceDate> getExpenseByVehicleAndServiceDateForUser(String userId) {
        return
                vehicleServiceLogRepository.getExpenseByVehicleAndServiceDate(userId);
    }

    public void deleteServiceLog(String serviceLogId) {
        vehicleServiceLogRepository.deleteById(serviceLogId);
    }

    public List<VehicleServiceLog> getAllServiceLogsForUserAndVehicle(User user, Vehicle vehicle) {
        Optional<UserVehicle> optionalUserVehicle = userVehicleRepository.findByUserAndVehicle(user, vehicle);
        if (optionalUserVehicle.isPresent()) {
            return vehicleServiceLogRepository.findByUserVehicleOrderByServiceDateDesc(optionalUserVehicle.get());
        }
        return null;
    }

    public List<ServiceLog> getAllServiceLogsForUser(User user) {
            return vehicleServiceLogRepository.getAllServiceLogsForUser(user.getId());
    }

    public void updateServiceLog(VehicleServiceLogRequest request, VehicleServiceLog existingServiceLog) {
        String totalCost = request.getServiceCost();
        if (totalCost != null && !totalCost.isEmpty()) {
            existingServiceLog.setServiceCost(Float.parseFloat(totalCost));
        }
        String odometerReading = request.getOdometerReading();
        if (odometerReading != null && !odometerReading.isEmpty()) {
            existingServiceLog.setOdometerReading(Integer.parseInt(odometerReading));
        }
        String serviceDate = request.getServiceDate();
        existingServiceLog.setServiceDate(serviceDate);

        existingServiceLog.setServiceSummary(request.getServiceSummary());
        existingServiceLog.setServiceNotes(request.getServiceNotes());
        existingServiceLog.setServiceProvider(request.getServiceProvider());
        existingServiceLog.setServiceType(request.getServiceType());
        String gallons = request.getGallons();
        if (gallons != null && !gallons.isEmpty()) {
            existingServiceLog.setGallons(Float.parseFloat(gallons));
        }
        vehicleServiceLogRepository.save(existingServiceLog);
    }

    public VehicleServiceLog addVehicleServiceLog(
            VehicleServiceLogRequest request
            , User user
            , Vehicle vehicle) {
        Optional<UserVehicle> optionalUserVehicle = userVehicleRepository.findByUserAndVehicle(user, vehicle);
        if (optionalUserVehicle.isEmpty()) {
            //throw exception indicating no user vehicle found!
        }
        VehicleServiceLog vehicleServiceLog = new VehicleServiceLog();
        vehicleServiceLog.setUserVehicle(optionalUserVehicle.get());
        vehicleServiceLog.setServiceType(request.getServiceType());
        String totalCost = request.getServiceCost();
        if (totalCost != null && !totalCost.isEmpty()) {
            vehicleServiceLog.setServiceCost(Float.parseFloat(totalCost));
        }
        String odometerReading = request.getOdometerReading();
        if (odometerReading != null && !odometerReading.isEmpty()) {
            vehicleServiceLog.setOdometerReading(Integer.parseInt(odometerReading));
        }
        String gallons = request.getGallons();
        if (gallons != null && !gallons.isEmpty()) {
            vehicleServiceLog.setGallons(Float.parseFloat(gallons));
        }
        String serviceDate = request.getServiceDate();
        vehicleServiceLog.setServiceDate(serviceDate);

        vehicleServiceLog.setServiceNotes(request.getServiceNotes());
        vehicleServiceLog.setServiceProvider(request.getServiceProvider());
        vehicleServiceLog.setServiceSummary(request.getServiceSummary());
        return vehicleServiceLogRepository.save(vehicleServiceLog);
    }

    public List<ExpenseByServiceDate> getTotalExpensesForAllVehiclesAndUserAndServiceType(String userId,String serviceType) {
        return
                vehicleServiceLogRepository.getTotalExpensesForAllVehiclesAndUserAndServiceType(userId,serviceType);
    }

    public List<ExpenseByVehicleAndServiceDate> getExpensesForModelAndUserAndServiceType(String userId, String serviceType) {
        return
                vehicleServiceLogRepository.getExpensesForModelAndUserAndServiceType(userId,serviceType);
    }

    public List<ExpenseByServiceTypeAndServiceDate> getExpensesForUserAndServiceType(String userId, String serviceType) {
        return
                vehicleServiceLogRepository.getExpensesForUserAndServiceType(userId,serviceType);
    }
}

package com.ambrygen.adx.controllers;

import com.ambrygen.adx.dto.ChartData;
import com.ambrygen.adx.dto.ServiceLog;
import com.ambrygen.adx.dto.VehicleServiceLogRequest;
import com.ambrygen.adx.dto.ADXResponseDTO;
import com.ambrygen.adx.models.*;
import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.services.ServiceLogService;
import com.ambrygen.adx.services.VehicleService;
import com.ambrygen.adx.services.security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
public class ServiceLogController {
    private final UserService userService;
    private final VehicleService vehicleService;

    private final ServiceLogService serviceLogService;

    @GetMapping(value = { "/users/{userId}/servicelog-summary", "/users/{userId}/service-types/{serviceType}/servicelog-summary" })
    public ResponseEntity<ADXResponseDTO> getServiceLogHistory(
            @PathVariable("userId") String userId,
            @PathVariable(value = "serviceType", required = false) String serviceType
    ) {
        Integer minServiceYear = serviceLogService.getMinServiceYear(userId);
        List<ExpenseByServiceDate> expenseByServideDateList;
        List<ExpenseByVehicleAndServiceDate> expenseByVehicleAndServideDateList;
        List<ExpenseByServiceTypeAndServiceDate> expenseByServiceTypeAndDateList;
        if(serviceType == null || "All Services".equals(serviceType)) {
            expenseByServideDateList =
                    serviceLogService.getExpenseByServiceDateForUser(userId);
            expenseByVehicleAndServideDateList =
                    serviceLogService.getExpenseByVehicleAndServiceDateForUser(userId);
            expenseByServiceTypeAndDateList =
                    serviceLogService.getExpenseByServiceTypeAndServiceDateForUser(userId);
        } else {
            expenseByServideDateList =
                    serviceLogService.getTotalExpensesForAllVehiclesAndUserAndServiceType(userId,serviceType);
            expenseByVehicleAndServideDateList =
                    serviceLogService.getExpensesForModelAndUserAndServiceType(userId,serviceType);
            expenseByServiceTypeAndDateList =
                    serviceLogService.getExpensesForUserAndServiceType(userId,serviceType);
        }

        ChartData chartData = new ChartData();
        chartData.setExpenseByServiceDateList(expenseByServideDateList);
        chartData.setExpenseByVehicleAndServiceDateList(expenseByVehicleAndServideDateList);
        chartData.setExpenseByServiceTypeAndServiceDateList(expenseByServiceTypeAndDateList);
        chartData.setMinServiceYear(minServiceYear.intValue());
        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", chartData);
        return ResponseEntity.ok(ADXResponseDTO);
    }

    @GetMapping("/users/{userId}/vehicles/{vehicleId}/servicelog-summary")
    public ResponseEntity<ADXResponseDTO> getServiceLogHistoryForVehicle(
            @PathVariable("userId") String userId,  @PathVariable("vehicleId") String vehicleId
    ) {
        List<ExpenseByServiceDate> expenseByServideDateList =
                serviceLogService.getExpenseByServiceDateForUserAndVehicle(userId,vehicleId);
        List<ExpenseByServiceTypeAndServiceDate> expenseByServiceTypeAndDateList =
                serviceLogService.getExpenseByServiceTypeAndServiceDateForUserAndVehicle(userId,vehicleId);
        ChartData chartData = new ChartData();
        //TODO Hardcode this year for now
        chartData.setMinServiceYear(1971);
        chartData.setExpenseByServiceDateList(expenseByServideDateList);
        chartData.setExpenseByServiceTypeAndServiceDateList(expenseByServiceTypeAndDateList);
        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", chartData);
        return ResponseEntity.ok(ADXResponseDTO);
    }

    @GetMapping("/users/{userId}/vehicles/{vehicleId}/servicelogs")
    public ResponseEntity<ADXResponseDTO> getAllServiceLogsForUserAndVehicle(
            @PathVariable("userId") String userId,
            @PathVariable("vehicleId") String vehicleId
    ) {
        Optional<User> optionalUser = userService.findById(userId);
        Optional<Vehicle> optionalVehicle = vehicleService.findById(vehicleId);

        if (optionalVehicle.isEmpty()) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "Vehicle not found.", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }
        if (optionalUser.isEmpty()) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "User not found.", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }
        List<VehicleServiceLog> serviceLogs =
                serviceLogService.getAllServiceLogsForUserAndVehicle(
                        optionalUser.get()
                        , optionalVehicle.get());

        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", serviceLogs);
        return ResponseEntity.ok(ADXResponseDTO);
    }

    @GetMapping("/users/{userId}/servicelogs")
    public ResponseEntity<ADXResponseDTO> getAllServiceLogsForUser(
            @PathVariable("userId") String userId
    ) {
        Optional<User> optionalUser = userService.findById(userId);

        if (optionalUser.isEmpty()) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "User not found.", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }
        Integer minServiceYear = serviceLogService.getMinServiceYear(userId);
        List<ServiceLog> serviceLogs =
                serviceLogService.getAllServiceLogsForUser(
                        optionalUser.get());

        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", serviceLogs);
        return ResponseEntity.ok(ADXResponseDTO);
    }

    @PostMapping("/users/{userId}/vehicles/{vehicleId}/servicelogs")
    public ResponseEntity<ADXResponseDTO> addServiceLog(
            @PathVariable("userId") String userId,
            @PathVariable("vehicleId") String vehicleId,
            @RequestBody VehicleServiceLogRequest request) {
        Optional<User> optionalUser = userService.findById(userId);
        Optional<Vehicle> optionalVehicle = vehicleService.findById(vehicleId);

        if (optionalVehicle.isEmpty()) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "Vehicle not found.", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }
        if (optionalUser.isEmpty()) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "User not found.", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }

        serviceLogService.addVehicleServiceLog(request, optionalUser.get(), optionalVehicle.get());

        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", "Service log added successfully.");
        return ResponseEntity.ok(ADXResponseDTO);
    }



    @PutMapping("/servicelogs/{serviceLogId}")
    public ResponseEntity<ADXResponseDTO> updateServiceLog(
            @PathVariable("serviceLogId") String serviceLogId,
            @RequestBody VehicleServiceLogRequest request) {

        Optional<VehicleServiceLog> optionalServiceLog = serviceLogService.findById(serviceLogId);
        if (optionalServiceLog.isEmpty()) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "Service log not found.", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }
        serviceLogService.updateServiceLog(request,optionalServiceLog.get());

        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", "Service log updated successfully.");
        return ResponseEntity.ok(ADXResponseDTO);
    }

    @DeleteMapping("/servicelogs/{serviceLogId}")
    public ResponseEntity<ADXResponseDTO> deleteServiceLog(
            @PathVariable("serviceLogId") String serviceLogId) {
        serviceLogService.deleteServiceLog(serviceLogId);
        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", "Service log deleted successfully.");
        return ResponseEntity.ok(ADXResponseDTO);
    }
}


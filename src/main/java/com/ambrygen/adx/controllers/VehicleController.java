package com.ambrygen.adx.controllers;

import com.ambrygen.adx.dto.*;
import com.ambrygen.adx.models.UserVehicle;
import com.ambrygen.adx.models.Vehicle;
import com.ambrygen.adx.models.VehicleServiceLog;
import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.services.ServiceLogService;
import com.ambrygen.adx.services.VehicleService;
import com.ambrygen.adx.services.security.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
public class VehicleController {
    private final UserService userService;
    private final VehicleService vehicleService;
    private final ServiceLogService serviceLogService;


    @PostMapping("/vehicles/vin")
    public ResponseEntity<ADXResponseDTO> decodeVIN(@Valid @RequestBody VINDecodeRequest request) {
        String vin = request.getVin();
        int year = request.getYear();
        //Check if this vin + year vehicle exists in our db.
        //If it exists then return that one instead of making remote API calls to NHTSA
        VehicleInfo vehicleInfo = vehicleService.findByVinAndYear(vin, year);
        if (vehicleInfo != null) {
            return ResponseEntity.ok(new ADXResponseDTO<>(false, "", vehicleInfo));
        }
        VehicleInfo nhtsaVehicleInfo = vehicleService.decodeVin(vin, String.valueOf(year));
        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", nhtsaVehicleInfo);
        return ResponseEntity.ok(ADXResponseDTO);
    }

    @GetMapping("/users/{userId}/vehicles/{userVehicleId}")
    public ResponseEntity<ADXResponseDTO> getVehicle(
            @PathVariable("userId") String userId,
            @PathVariable("userVehicleId") String userVehicleId) {
        Optional<User> optionalUser = userService.findById(userId);
        if ( optionalUser.isEmpty()) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "User not found.", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }
        Optional<UserVehicle> userVehicle = vehicleService.getUserVehicle(userVehicleId);
        if ( userVehicle.isEmpty()) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "User Vehicle not found.", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }

        if (! userVehicle.get().getUser().getId().equalsIgnoreCase(userId)) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "This vehicle does not belong to the user", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }
        UserVehicleData userVehicleData = vehicleService.getUserVehicleData(userVehicle.get());

        Vehicle vehicle = userVehicle.get().getVehicle();
        if (vehicle.getTrim() == null || vehicle.getTrim().isEmpty()) {
            vehicle.setTrim(vehicle.getSeries());
        }
        VehicleInfo vehicleInfo = vehicleService.getVehicleInfo(userVehicleData, vehicle);

        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", vehicleInfo);
        return ResponseEntity.ok(ADXResponseDTO);
    }

    @DeleteMapping("/users/{userId}/vehicles/{vehicleId}")
    public ResponseEntity<ADXResponseDTO> deleteVehicle(
            @PathVariable("userId") String userId,
            @PathVariable("vehicleId") String vehicleId) {

        Optional<User> optionalUser = userService.findById(userId);
        Optional<Vehicle> optionalVehicle = vehicleService.findById(vehicleId);
        User user = optionalUser.get();
        Vehicle vehicle = optionalVehicle.get();

        //First delete all associated service logs
        List<VehicleServiceLog> serviceLogs = serviceLogService.getAllServiceLogsForUserAndVehicle(user,vehicle);
        for(VehicleServiceLog serviceLog: serviceLogs) {
            serviceLogService.deleteServiceLog(serviceLog.getId());
        }
        vehicleService.deleteUserVehicle(user, vehicle);
        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", "User vehicle deleted successfully.");
        return ResponseEntity.ok(ADXResponseDTO);
    }

    @PatchMapping("/users/{userId}/vehicles/{vehicleId}")
    public ResponseEntity<ADXResponseDTO> updateVehicle(
            @PathVariable("userId") String userId,
            @PathVariable("vehicleId") String vehicleId,
            @RequestBody UpdateVehicleRequest request) {
        Optional<User> optionalUser = userService.findById(userId);
        Vehicle vehicle = getVehicle(vehicleId);
        if (vehicle == null) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "Vehicle not found.", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }
        if (optionalUser.isEmpty()) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "User not found.", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }
        Optional<UserVehicle> userVehicleOptional = vehicleService.getUserVehicle(optionalUser.get(), vehicle);
        if (userVehicleOptional.isEmpty()) {
            ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(true, "Could not find vehicle associated with the user.", "");
            return ResponseEntity.ok(ADXResponseDTO);
        }
        UserVehicle userVehicle = userVehicleOptional.get();
        vehicleService.updatedUserVehicle(userVehicle
                , request.getLicensePlate()
                , request.getPurchasePrice()
                , request.getPurchaseDate()
                , request.getOdometerReading());
        vehicleService.saveUserVehicle(userVehicle);
        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", "Vehicle details updated successfully.");
        return ResponseEntity.ok(ADXResponseDTO);
    }

    @PostMapping("/users/{userId}/vehicles")
    public ResponseEntity<ADXResponseDTO> addVehicle(
            @PathVariable("userId") String userId,
            @RequestBody AddVehicleRequest request) {
        Optional<User> optionalUser = userService.findById(userId);
        Vehicle vehicle = getVehicle(request);
        Vehicle addedVehicle = vehicleService.saveVehicle(request, optionalUser.get(), vehicle);
        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", addedVehicle);
        return ResponseEntity.ok(ADXResponseDTO);
    }

    private Vehicle getVehicle(String vehicleId) {
        //Get existing vehicle.
        Optional<Vehicle> optionalVehicle = vehicleService.findById(vehicleId);
        return optionalVehicle.get();
    }

    private Vehicle getVehicle(AddVehicleRequest request) {
        String vehicleId = request.getVehicleId();
        if (vehicleId != null && !vehicleId.isEmpty()) {
            //Get existing vehicle.
            Vehicle vehicle = getVehicle(vehicleId);
            if (vehicle != null) {
                return vehicle;
            }
        }
        String vin = request.getVin();
        String year = request.getYear();
        String manufacturer = request.getManufacturer();
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(vin == null || vin.isEmpty() ? "UNKNOWN" : vin);
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setBodyClass(request.getBodyClass());
        vehicle.setManufacturer(manufacturer == null || manufacturer.isEmpty() ? "UNKNOWN" : manufacturer);
        vehicle.setPlantCity(request.getPlantCity());
        vehicle.setPlantCountry(request.getPlantCountry());
        vehicle.setYear(Integer.parseInt(year));
        vehicle.setTrim(request.getTrim());
        vehicle.setSeries(request.getSeries());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setEngineConfiguration(request.getEngineConfiguration());
        vehicle.setFuelTypePrimary(request.getFuelTypePrimary());
        String displacementCc = request.getDisplacementCC();
        String displacementLiter = request.getDisplacementLiter();
        String cylinders = request.getEngineCylinders();
        String engineHP = request.getEngineHP();
        vehicle.setDisplacementCc(Float.parseFloat(
                displacementCc == null || displacementCc.isEmpty() ? "250": displacementCc));
        vehicle.setDisplacementLiter(Float.parseFloat(
                displacementLiter == null || displacementLiter.isEmpty() ? "1000": displacementLiter));
        vehicle.setEngineCylinders(Integer.parseInt(cylinders == null || cylinders.isEmpty() ? "2": cylinders));
        vehicle.setEngineHp(Float.parseFloat(engineHP == null || engineHP.isEmpty() ? "100": engineHP));

        return vehicle;
    }

    @GetMapping("/users/{userId}/vehicles")
    public ResponseEntity<ADXResponseDTO> getVehicles(@PathVariable("userId") String userId) {
        Optional<User> optionalUser = userService.findById(userId);
        List<UserVehicle> userVehicles = vehicleService.getUserVehicles(optionalUser.get());
        List<VehicleInfo> vehicles = new ArrayList<>();
        for (UserVehicle userVehicle : userVehicles) {
            UserVehicleData userVehicleData = vehicleService.getUserVehicleData(userVehicle);
            Vehicle vehicle = userVehicle.getVehicle();
            if (vehicle.getTrim() == null || vehicle.getTrim().isEmpty()) {
                vehicle.setTrim(vehicle.getSeries());
            }
            VehicleInfo vehicleInfo = vehicleService.getVehicleInfo(userVehicleData, vehicle);
            vehicles.add(vehicleInfo);
        }
        ADXResponseDTO ADXResponseDTO = new ADXResponseDTO<>(false, "", vehicles);
        return ResponseEntity.ok(ADXResponseDTO);
    }
}


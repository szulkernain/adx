package com.ambrygen.adx.services;

import com.ambrygen.adx.dto.AddVehicleRequest;
import com.ambrygen.adx.dto.UserVehicleData;
import com.ambrygen.adx.dto.VehicleInfo;
import com.ambrygen.adx.errors.InvalidVINException;
import com.ambrygen.adx.models.UserVehicle;
import com.ambrygen.adx.models.Vehicle;
import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.repositories.UserVehicleRepository;
import com.ambrygen.adx.repositories.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class VehicleService {
    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);
    private final RestTemplate restTemplate;

    private final VehicleRepository vehicleRepository;
    private final UserVehicleRepository userVehicleRepository;


    public VehicleService(RestTemplateBuilder restTemplateBuilder, VehicleRepository vehicleRepository,
                          UserVehicleRepository userVehicleRepository) {
        this.restTemplate = restTemplateBuilder.build();
        this.vehicleRepository = vehicleRepository;
        this.userVehicleRepository = userVehicleRepository;
    }


    public Optional<Vehicle> findById(String vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    public VehicleInfo findByVinAndYear(String vin, int year) {

        Optional<Vehicle> vehicleOptional = vehicleRepository.findByVinAndYear(vin, Integer.valueOf(year));
        if (vehicleOptional.isEmpty()) {
            return null;
        }

        VehicleInfo vehicleInfo = getVehicleInfoFromVehicle(vehicleOptional.get());
        return vehicleInfo;
    }

    private VehicleInfo getVehicleInfoFromVehicle(Vehicle vehicle) {
        VehicleInfo vehicleInfo = new VehicleInfo();
        vehicleInfo.setId(vehicle.getId());
        vehicleInfo.setManufacturer(vehicle.getManufacturer());
        vehicleInfo.setVin(vehicle.getVin());
        vehicleInfo.setYear(vehicle.getYear());
        vehicleInfo.setBodyClass(vehicle.getBodyClass());
        vehicleInfo.setPlantCity(vehicle.getPlantCity());
        vehicleInfo.setPlantCountry(vehicle.getPlantCountry());
        vehicleInfo.setMake(vehicle.getMake());
        vehicleInfo.setModel(vehicle.getModel());
        vehicleInfo.setVehicleType(vehicle.getVehicleType());
        vehicleInfo.setTrim(vehicle.getTrim());
        vehicleInfo.setSeries(vehicle.getSeries());
        vehicleInfo.setDisplacementCc(vehicle.getDisplacementCc());
        vehicleInfo.setDisplacementLiter(vehicle.getDisplacementLiter());
        vehicleInfo.setEngineConfiguration(vehicle.getEngineConfiguration());
        vehicleInfo.setFuelTypePrimary(vehicle.getFuelTypePrimary());
        vehicleInfo.setEngineCylinders(vehicle.getEngineCylinders());
        vehicleInfo.setEngineHp(vehicle.getEngineHp());
        vehicleInfo.setVehicleName(getVehicleName(vehicle));
        return vehicleInfo;
    }

    private String getVehicleName(Vehicle vehicle) {
        return getVehicleName(vehicle.getYear(), vehicle.getMake(), vehicle.getModel());
    }

    private String getVehicleName(int year, String make, String model) {
        return year + " - " + make + " - " + model;
    }

    public VehicleInfo getVehicleInfo(UserVehicleData userVehicleData, Vehicle vehicle) {
        VehicleInfo vehicleInfo = getVehicleInfoFromVehicle(vehicle);
        vehicleInfo.setOdometerReading(userVehicleData.getOdometerReading());
        vehicleInfo.setLicensePlate(userVehicleData.getLicensePlate());
        vehicleInfo.setPurchasePrice(userVehicleData.getPurchasePrice());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        vehicleInfo.setPurchaseDate(userVehicleData.getPurchaseDate());
        vehicleInfo.setImageFileName(userVehicleData.getImageFileName());
        vehicleInfo.setUserVehicleId(userVehicleData.getUserVehicleId());
        //\(vehicle.year) - \(vehicle.make) - \(vehicle.model)
        vehicleInfo.setVehicleName(getVehicleName(vehicle));
        return vehicleInfo;
    }

    public void updateImageFileName(String userVehicleId, String imageFileName) {
        Optional<UserVehicle> optionalUserVehicle = userVehicleRepository.findById(userVehicleId);
        if (optionalUserVehicle.isPresent()) {
            UserVehicle userVehicle = optionalUserVehicle.get();
            userVehicle.setImageFileName(imageFileName);
            userVehicleRepository.save(userVehicle);
        }
    }

    public VehicleInfo decodeVin(String vin, String year) {
        String url = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevinvaluesextended/" + vin + "?format=json&modelyear=" + year;
        Map theMap = this.restTemplate.getForObject(
                "https://vpic.nhtsa.dot.gov/api/vehicles/decodevinvaluesextended/{vin}?format=json&modelyear={year}"
                , Map.class, vin, year);
        VehicleInfo vehicleInfo = new VehicleInfo();
        ArrayList<Map> results = (ArrayList<Map>) theMap.get("Results");
        if (!results.isEmpty()) {
            Map vehicleInformation = (Map) results.get(0);
            //BodyClass, DisplacementCC, EngineCylinders, EngineHP, FuelTypePrimary,
            //Make, Manufacturer, Model, ModelYear, PlantCity, PlantCountry,
            //Trim, VIN, VehicleType, ErrorText
            String errorCode = (String) vehicleInformation.get("ErrorCode");
            String errorText = (String) vehicleInformation.get("ErrorText");
            if (!errorCode.equalsIgnoreCase("0")) {
                logger.warn("VIN: " + vin + ", Year:  " + year + " :: " + errorText);
                throw new InvalidVINException("Invalid VIN or invalid VIN and Year combination.");
            }
            String bodyClass = (String) vehicleInformation.get("BodyClass");
            String make = (String) vehicleInformation.get("Make");
            String manufacturer = (String) vehicleInformation.get("Manufacturer");
            String model = (String) vehicleInformation.get("Model");
            String modelYear = (String) vehicleInformation.get("ModelYear");
            String plantCity = (String) vehicleInformation.get("PlantCity");
            String plantCountry = (String) vehicleInformation.get("PlantCountry");
            String vehicleVin = (String) vehicleInformation.get("VIN");
            String trim = (String) vehicleInformation.get("Trim");
            String series = (String) vehicleInformation.get("Series");
            String vehicleType = (String) vehicleInformation.get("VehicleType");
            String displacementCC = (String) vehicleInformation.get("DisplacementCC");
            String displacementL = (String) vehicleInformation.get("DisplacementL");
            String engineCylinders = (String) vehicleInformation.get("EngineCylinders");
            String engineConfiguration = (String) vehicleInformation.get("EngineConfiguration");
            String engineHP = (String) vehicleInformation.get("EngineHP");
            String fuelTypePrimary = (String) vehicleInformation.get("FuelTypePrimary");

            vehicleInfo.setManufacturer(manufacturer);
            vehicleInfo.setVin(vehicleVin);
            vehicleInfo.setYear(Integer.parseInt(modelYear));
            vehicleInfo.setBodyClass(bodyClass);
            vehicleInfo.setPlantCity(plantCity);
            vehicleInfo.setPlantCountry(plantCountry);
            vehicleInfo.setMake(make);
            vehicleInfo.setModel(model);
            vehicleInfo.setTrim(trim);
            vehicleInfo.setSeries(series);
            vehicleInfo.setVehicleType(vehicleType);
            //Engine Specifications
            vehicleInfo.setFuelTypePrimary(fuelTypePrimary);
            vehicleInfo.setEngineConfiguration(engineConfiguration);
            if(displacementCC != null && !displacementCC.isEmpty()) {
                vehicleInfo.setDisplacementCc(Float.parseFloat(displacementCC));
            }

            if (displacementL != null && !displacementL.isEmpty()) {
                vehicleInfo.setDisplacementLiter(Float.parseFloat(displacementL));
            }
            if(engineCylinders != null && !engineCylinders.isEmpty()) {
                vehicleInfo.setEngineCylinders(Integer.parseInt(engineCylinders));
            }
            if (engineHP != null && !engineHP.isEmpty()) {
                vehicleInfo.setEngineHp(Float.parseFloat(engineHP));
            }
            vehicleInfo.setVehicleName(getVehicleName(vehicleInfo.getYear() ,vehicleInfo.getMake() , vehicleInfo.getModel()));
        }
        return vehicleInfo;
    }

    public void deleteUserVehicle(User user, Vehicle vehicle) {
        Optional<UserVehicle> userVehicleOptional = userVehicleRepository.findByUserAndVehicle(user, vehicle);
        UserVehicle userVehicle = userVehicleOptional.get();
        if (userVehicle != null) {
            userVehicleRepository.delete(userVehicle);
        }
        vehicleRepository.delete(vehicle);
    }

    public void updatedUserVehicle(UserVehicle userVehicle,
                                   String licensePlate, String purchasePrice,
                                   String purchaseDate, String odometerReading) {
        userVehicle.setLicensePlate(licensePlate);
        if (purchasePrice != null && !purchasePrice.isEmpty() && !purchasePrice.isBlank()) {
            try {
                userVehicle.setPurchasePrice(Float.parseFloat(purchasePrice));
            }catch(NumberFormatException nfe) {
                userVehicle.setPurchasePrice(null);
            }
        } else {
            userVehicle.setPurchasePrice(null);
        }
        if (odometerReading != null && !odometerReading.isEmpty() && !odometerReading.isBlank()) {
            try {
                userVehicle.setOdometerReading(Integer.parseInt(odometerReading));
            }catch(NumberFormatException nfe) {
                userVehicle.setOdometerReading(0);
            }
        } else {
            userVehicle.setOdometerReading(0);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        userVehicle.setPurchaseDate(purchaseDate);
    }

    public Vehicle saveVehicle(AddVehicleRequest addVehicleRequest, User user, Vehicle vehicle) {
        Vehicle newVehicle = vehicle;
        if (vehicle.getId() == null) {
            newVehicle = vehicleRepository.save(vehicle);
        }
        UserVehicle userVehicle = new UserVehicle();
        userVehicle.setUser(user);
        userVehicle.setVehicle(newVehicle);
        String odometerReading = addVehicleRequest.getOdometerReading();
        updatedUserVehicle(userVehicle, addVehicleRequest.getLicensePlate(),
                addVehicleRequest.getPurchasePrice()
                , addVehicleRequest.getPurchaseDate()
                , odometerReading == null || odometerReading.isEmpty() ? "0": odometerReading);
        saveUserVehicle(userVehicle);
        vehicle.setVehicleName(getVehicleName(vehicle));
        return newVehicle;
    }

    public UserVehicle saveUserVehicle(UserVehicle userVehicle) {
        return userVehicleRepository.save(userVehicle);
    }

    public Optional<UserVehicle> getUserVehicle(User user, Vehicle vehicle) {
        return userVehicleRepository.findByUserAndVehicle(user, vehicle);
    }

    public Optional<UserVehicle> getUserVehicle(String userVehicleId) {
        return userVehicleRepository.findById(userVehicleId);
    }

    public List<UserVehicle> getUserVehicles(User user) {
        return userVehicleRepository.findByUserOrderByPurchaseDateDesc(user);
    }

    public UserVehicleData getUserVehicleData(UserVehicle userVehicle) {
        UserVehicleData userVehicleData = new UserVehicleData();
        userVehicleData.setLicensePlate(userVehicle.getLicensePlate());
        userVehicleData.setOdometerReading(userVehicle.getOdometerReading());
        userVehicleData.setPurchaseDate(userVehicle.getPurchaseDate());
        userVehicleData.setPurchasePrice(userVehicle.getPurchasePrice());
        userVehicleData.setImageFileName(userVehicle.getImageFileName());
        userVehicleData.setUserVehicleId(userVehicle.getId());
        return userVehicleData;
    }
}

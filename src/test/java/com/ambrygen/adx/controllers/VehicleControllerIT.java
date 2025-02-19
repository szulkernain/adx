package com.ambrygen.adx.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ambrygen.adx.ADXApplicationIntegrationTest;
import com.ambrygen.adx.dto.ADXResponseDTO;
import com.ambrygen.adx.dto.VehicleInfo;
import com.ambrygen.adx.dto.VehicleServiceLogRequest;
import com.ambrygen.adx.models.Vehicle;
import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.services.security.UserService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
public class VehicleControllerIT extends ADXApplicationIntegrationTest {
    @Autowired
    private UserService userService;


    @Value("${ADX_USERNAME:awesomefruits}")
    protected String userName;
    @Value("${ADX_PASSWORD:applesoranges}")
    protected String password;

    @Autowired
    public VehicleControllerIT(RestTemplateBuilder builder) {
        super(builder);
    }

    @PostConstruct
    public void signIn() {
        this.login(userName, password);
    }

    private User user;
    private Vehicle vehicle;

    @Test
    @Order(1)
    void createVehicleTest() {
        user = userService.getUserByEmailAddress(userName);
        assertThat(user.getId()).isNotEmpty();
        vehicle = createVehicle(user.getId());
        assertThat(vehicle).isNotNull();
    }


    @Test
    @Order(2)
    void getUserVehiclesTest() {
        user = userService.getUserByEmailAddress(userName);
        List<VehicleInfo> vehicles = getUserVehicles(user);
        assertThat(vehicles).isNotNull();
        assertThat(vehicles.size()).isGreaterThan(0);
    }

    @Test
    @Order(3)
    void createServiceLogTest() throws Exception {
        user = userService.getUserByEmailAddress(userName);
        List<VehicleInfo> vehicles = getUserVehicles(user);
        for (VehicleInfo vehicle : vehicles) {
            String message = addServiceLog(user, vehicle);
            assertThat(message).isEqualTo("Service log added successfully.");
        }
    }

    @Test
    @Order(4)
    void deleteVehicleTest() throws Exception {
        user = userService.getUserByEmailAddress(userName);
        List<VehicleInfo> vehicles = getUserVehicles(user);
        for (VehicleInfo vehicle : vehicles) {
            String message = deleteVehicle(user, vehicle);
            assertThat(message).isEqualTo("User vehicle deleted successfully.");
        }
    }

    private String deleteVehicle(User user, VehicleInfo vehicle) {
        EntityExchangeResult<ADXResponseDTO<String>> result =
                webClient.delete().uri("/api/users/{userId}/vehicles/{vehicleId}", user.getId(), vehicle.getId())
                        .header("Authorization", "Bearer " + this.jwt)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(new ParameterizedTypeReference<ADXResponseDTO<String>>() {
                        })
                        .returnResult();
        ADXResponseDTO response = result.getResponseBody();
        assertThat(response).isNotNull();
        return (String) response.getResponse();
    }

    private String addServiceLog(User user, VehicleInfo vehicleInfo) throws Exception {
        VehicleServiceLogRequest serviceLog = new VehicleServiceLogRequest();
        serviceLog.setServiceDate("2024-01-02");
        serviceLog.setServiceType("Brakes");
        serviceLog.setServiceSummary("Changed brakes");

        ObjectMapper mapper = new ObjectMapper();

        String serviceLogPayload = mapper.writeValueAsString(serviceLog);
        EntityExchangeResult<ADXResponseDTO<String>> result =
                webClient.post().uri("/api/users/{userId}/vehicles/{vehicleId}/servicelogs", user.getId(), vehicleInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + this.jwt)
                        .bodyValue(serviceLogPayload)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(new ParameterizedTypeReference<ADXResponseDTO<String>>() {
                        })
                        .returnResult();
        ADXResponseDTO response = result.getResponseBody();
        assertThat(response).isNotNull();
        return (String) response.getResponse();
    }

    private List<VehicleInfo> getUserVehicles(User user) {
        EntityExchangeResult<ADXResponseDTO<List<VehicleInfo>>> result =
                webClient.get().uri("/api/users/{userId}/vehicles", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + this.jwt)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(new ParameterizedTypeReference<ADXResponseDTO<List<VehicleInfo>>>() {
                        })
                        .returnResult();
        ADXResponseDTO response = result.getResponseBody();
        assertThat(response).isNotNull();
        List<VehicleInfo> vehicles = (List<VehicleInfo>) response.getResponse();
        return vehicles;
    }

    private Vehicle createVehicle(String userId) {
        String payload = Utils.readFileContents("/payloads/add_vehicle_request.json");
        assertThat(payload).isNotEmpty();
        HashMap<String, String> replacements = new HashMap<String, String>();

        replacements.put("userId", userId);
        String addVehiclePayload = Utils.replaceTokensWithValues(payload, replacements);

        assertThat(addVehiclePayload).isNotEmpty();

        EntityExchangeResult<ADXResponseDTO<Vehicle>> result =
                webClient.post().uri("/api/users/{userId}/vehicles", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + this.jwt)
                        .bodyValue(addVehiclePayload)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(new ParameterizedTypeReference<ADXResponseDTO<Vehicle>>() {
                        })
                        .returnResult();
        ADXResponseDTO response = result.getResponseBody();
        assertThat(response).isNotNull();

        Vehicle vehicle = (Vehicle) response.getResponse();
        return vehicle;
    }
}

package com.ambrygen.adx.repositories;

import com.ambrygen.adx.dto.ServiceLog;
import com.ambrygen.adx.models.*;
import com.ambrygen.adx.models.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleServiceLogRepository extends CrudRepository<VehicleServiceLog, String> {
    List<VehicleServiceLog> findByUserVehicleOrderByServiceDateDesc(UserVehicle userVehicle);

    @Query(value =
            "SELECT a.id" +
                    ", a.service_date as serviceDate" +
                    ", a.service_type as serviceType" +
                    ", a.service_summary as serviceSummary" +
                    ", a.odometer_reading as odometerReading" +
                    ", a.gallons as gallons" +
                    ", a.service_cost as serviceCost" +
                    ", a.service_notes as serviceNotes" +
                    ", a.service_provider as serviceProvider" +
                    ", a.service_provider_city as serviceProviderCity" +
                    ", a.service_provider_province as serviceProviderProvince" +
                    ", a.service_provider_country as serviceProviderCountry" +
                    ", c.make as make" +
                    ", c.model as model" +
                    " ,b.id AS userVehicleId" +
                    " FROM vehicle_service_logs AS a" +
                    ", user_vehicles AS b" +
                    ", vehicles AS c" +
                    " WHERE a.user_vehicle_id = b.id" +
                    " AND b.vehicle_id = c.id" +
                    " AND b.user_id = :userId" +
                    " ORDER BY serviceDate DESC"
            , nativeQuery = true)
    List<ServiceLog> getAllServiceLogsForUser(@Param("userId") String userId);
    //For all vehicle for a user
    @Query(value =
            "SELECT uuid() AS id" +
                    ", sum(a.service_cost) AS totalCost" +
                    " ,a.service_date AS serviceDate" +
                    " FROM vehicle_service_logs AS a" +
                    ", user_vehicles AS b" +
                    ", vehicles AS c" +
                    " WHERE a.user_vehicle_id = b.id" +
                    " AND b.vehicle_id = c.id" +
                    " AND b.user_id = :userId" +
                    " GROUP BY serviceDate ORDER BY serviceDate"
            , nativeQuery = true)
    List<ExpenseByServiceDate> getExpenseByServiceDate(@Param("userId") String userId);


    @Query(value =
            "SELECT uuid() AS id" +
                    ", sum(a.service_cost) AS totalCost" +
                    " ,a.service_date AS serviceDate" +
                    " FROM vehicle_service_logs AS a" +
                    ", user_vehicles AS b" +
                    ", vehicles AS c" +
                    " WHERE a.user_vehicle_id = b.id" +
                    " AND b.vehicle_id = c.id" +
                    " AND b.user_id = :userId" +
                    " AND a.service_type = :serviceType" +
                    " GROUP BY serviceDate ORDER BY serviceDate"
            , nativeQuery = true)
    List<ExpenseByServiceDate> getTotalExpensesForAllVehiclesAndUserAndServiceType(@Param("userId") String userId, @Param("serviceType") String serviceType);

    @Query(value =
            "SELECT min(year(a.service_date)) AS minServiceYear" +
                    " FROM vehicle_service_logs AS a" +
                    ", user_vehicles AS b" +
                    ", vehicles AS c" +
                    " WHERE a.user_vehicle_id = b.id" +
                    " AND b.vehicle_id = c.id" +
                    " AND b.user_id = :userId"
            , nativeQuery = true)
    Integer getMinServiceYear(@Param("userId") String userId);

    //For a specific vehicle of a user
    @Query(value =
            "SELECT uuid() AS id" +
                    ", sum(a.service_cost) AS totalCost" +
                    " ,a.service_date AS serviceDate" +
                    " FROM vehicle_service_logs AS a" +
                    ", user_vehicles AS b" +
                    ", vehicles AS c" +
                    " WHERE a.user_vehicle_id = b.id" +
                    " AND b.vehicle_id = c.id" +
                    " AND a.user_vehicle_id = :vehicleId" +
                    " AND b.user_id = :userId" +
                    " GROUP BY serviceDate ORDER BY serviceDate"
            , nativeQuery = true)
    List<ExpenseByServiceDate> getExpenseByServiceDateForVehicle(@Param("userId") String userId,
                                                                 @Param("vehicleId") String vehicleId);

    @Query(value =
            "SELECT uuid() AS id" +
                    ", sum(a.service_cost) AS totalCost" +
                    " ,a.service_date AS serviceDate" +
                    " ,concat(c.make,'-',c.model) AS makeModel" +
                    " FROM vehicle_service_logs AS a" +
                    ", user_vehicles AS b" +
                    ", vehicles AS c" +
                    " WHERE a.user_vehicle_id = b.id" +
                    " AND b.vehicle_id = c.id" +
                    " AND b.user_id = :userId" +
                    " GROUP BY serviceDate, makeModel ORDER BY serviceDate"
            , nativeQuery = true)
    List<ExpenseByVehicleAndServiceDate> getExpenseByVehicleAndServiceDate(@Param("userId") String userId);


    @Query(value =
            "SELECT uuid() AS id" +
                    ", sum(a.service_cost) AS totalCost" +
                    " ,a.service_date AS serviceDate" +
                    " ,concat(c.make,'-',c.model) AS makeModel" +
                    " FROM vehicle_service_logs AS a" +
                    ", user_vehicles AS b" +
                    ", vehicles AS c" +
                    " WHERE a.user_vehicle_id = b.id" +
                    " AND b.vehicle_id = c.id" +
                    " AND b.user_id = :userId" +
                    " AND a.service_type = :serviceType" +
                    " GROUP BY serviceDate, makeModel ORDER BY serviceDate"
            , nativeQuery = true)
    List<ExpenseByVehicleAndServiceDate> getExpensesForModelAndUserAndServiceType(@Param("userId") String userId, @Param("serviceType") String serviceType);


    @Query(value =
            "select uuid() AS id" +
                    " ,a.service_date as serviceDate" +
                    " ,sum(case when a.service_type = 'Gas' then a.service_cost end) as gasCost" +
                    " ,sum(case when a.service_type = 'Other' then a.service_cost end) as otherCost" +
                    " ,sum(case when a.service_type = 'Brakes' then a.service_cost end) as brakesCost" +
                    " ,sum(case when a.service_type = 'Lights' then a.service_cost end) as lightsCost" +
                    " ,sum(case when a.service_type = 'Tires' then a.service_cost end) as tiresCost" +
                    " ,sum(case when a.service_type = 'Mods' then a.service_cost end) as modsCost" +
                    " ,sum(case when a.service_type = 'Oil Change' then a.service_cost end) as oilChangeCost" +
                    " from vehicle_service_logs AS a" +
                    " ,user_vehicles AS b" +
                    " ,vehicles AS c" +
                    " where  a.user_vehicle_id = b.id" +
                    " AND b.vehicle_id = c.id" +
                    " AND b.user_id = :userId" +
                    " group by serviceDate" +
                    " order by serviceDate"
            , nativeQuery = true)
    List<ExpenseByServiceTypeAndServiceDate> getExpenseByServiceTypeAndServiceDate(@Param("userId") String userId);


    @Query(value =
            "select uuid() AS id" +
                    " ,a.service_date as serviceDate" +
                    " ,sum(case when a.service_type = 'Gas' then a.service_cost end) as gasCost" +
                    " ,sum(case when a.service_type = 'Other' then a.service_cost end) as otherCost" +
                    " ,sum(case when a.service_type = 'Brakes' then a.service_cost end) as brakesCost" +
                    " ,sum(case when a.service_type = 'Lights' then a.service_cost end) as lightsCost" +
                    " ,sum(case when a.service_type = 'Tires' then a.service_cost end) as tiresCost" +
                    " ,sum(case when a.service_type = 'Mods' then a.service_cost end) as modsCost" +
                    " ,sum(case when a.service_type = 'Oil Change' then a.service_cost end) as oilChangeCost" +
                    " from vehicle_service_logs AS a" +
                    " ,user_vehicles AS b" +
                    " ,vehicles AS c" +
                    " where  a.user_vehicle_id = b.id" +
                    " AND b.vehicle_id = c.id" +
                    " AND b.user_id = :userId" +
                    " AND a.service_type = :serviceType" +
                    " group by serviceDate" +
                    " order by serviceDate"
            , nativeQuery = true)
    List<ExpenseByServiceTypeAndServiceDate> getExpensesForUserAndServiceType(@Param("userId") String userId,@Param("serviceType") String serviceType);


    @Query(value =
            "select uuid() AS id" +
                    " ,a.service_date as serviceDate" +
                    " ,sum(case when a.service_type = 'Gas' then a.service_cost end) as gasCost" +
                    " ,sum(case when a.service_type = 'Other' then a.service_cost end) as otherCost" +
                    " ,sum(case when a.service_type = 'Brakes' then a.service_cost end) as brakesCost" +
                    " ,sum(case when a.service_type = 'Lights' then a.service_cost end) as lightsCost" +
                    " ,sum(case when a.service_type = 'Tires' then a.service_cost end) as tiresCost" +
                    " ,sum(case when a.service_type = 'Mods' then a.service_cost end) as modsCost" +
                    " ,sum(case when a.service_type = 'Oil Change' then a.service_cost end) as oilChangeCost" +
                    " from vehicle_service_logs AS a" +
                    " ,user_vehicles AS b" +
                    " ,vehicles AS c" +
                    " where  a.user_vehicle_id = b.id" +
                    " AND b.vehicle_id = c.id" +
                    " AND a.user_vehicle_id = :vehicleId" +
                    " AND b.user_id = :userId" +
                    " group by serviceDate" +
                    " order by serviceDate"
            , nativeQuery = true)
    List<ExpenseByServiceTypeAndServiceDate> getExpenseByServiceTypeAndServiceDateForVehicle(@Param("userId") String userId,
                                                                                             @Param("vehicleId") String vehicleId);
}

package com.ambrygen.adx.models;

/**
 * select sum(a.service_cost), a.service_type, c.make,c.model,month(a.service_date),year(a.service_date)
 * from vehicle_service_logs a, user_vehicles b, vehicles c
 * where a.user_vehicle_id = b.id
 * and b.vehicle_id = c.id
 * group by service_type, c.make, c.model, month(a.service_date),year(a.service_date)
 */
public interface ExpenseByServiceDate {
     String getId();

     Float getTotalCost();

     String getServiceDate();
}

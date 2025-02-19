package com.ambrygen.adx.dto;

import com.ambrygen.adx.models.ExpenseByServiceDate;
import com.ambrygen.adx.models.ExpenseByServiceTypeAndServiceDate;
import com.ambrygen.adx.models.ExpenseByVehicleAndServiceDate;
import lombok.Data;

import java.util.List;

@Data
public class ChartData {
    List<ExpenseByServiceDate> expenseByServiceDateList;
    List<ExpenseByVehicleAndServiceDate> expenseByVehicleAndServiceDateList;
    List<ExpenseByServiceTypeAndServiceDate> expenseByServiceTypeAndServiceDateList;
    Integer minServiceYear;
}

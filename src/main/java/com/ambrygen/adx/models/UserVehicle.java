package com.ambrygen.adx.models;

import com.ambrygen.adx.models.security.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_vehicles")
@Getter
@Setter
@NoArgsConstructor
public class UserVehicle extends Auditable<String> {
    @ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.PERSIST)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column
    private String licensePlate;
    @Column
    private Integer odometerReading;
    @Column
    private Float purchasePrice;
    //Changed data type of purchaseDate from LocalDate to String. The purchase_date column in table is of type DATE.
    //When data type was string, even if the date passed is 2023-07-27, JDBC/Hibernate was probably converting it to 2023-07-26
    //After making this a String data type, the value stored in database is same as what was entered by user. So if user
    //says purchase date is 2023-07-27 then in the database also it gets stored as 2023-07-27
    //Got this hint/help from stackoverflow:
    // https://stackoverflow.com/questions/64263132/java-how-do-i-prevent-a-localdate-shifting-back-one-day-to-mysql-date
    @Column
    private String purchaseDate;

    @Column
    private String imageFileName;
    public UserVehicle(User user, Vehicle vehicle) {
        this.user = user;
        this.vehicle = vehicle;
    }
}

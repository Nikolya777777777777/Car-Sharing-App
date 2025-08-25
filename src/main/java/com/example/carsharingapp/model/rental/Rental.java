package com.example.carsharingapp.model.rental;

import com.example.carsharingapp.model.car.Car;
import com.example.carsharingapp.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "rentals")
@SQLDelete(sql = "UPDATE rentals SET is_deleted=true where id=?")
@SQLRestriction(value = "is_deleted=false")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime rentalDate;
    @Column(nullable = false)
    private LocalDateTime returnDate;
    private LocalDateTime actualReturnDate;
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}

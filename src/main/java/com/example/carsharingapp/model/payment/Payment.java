package com.example.carsharingapp.model.payment;

import com.example.carsharingapp.model.enums.PaymentType;
import com.example.carsharingapp.model.enums.Status;
import com.example.carsharingapp.model.rental.Rental;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@SQLDelete(sql = "UPDATE payments SET is_deleted = true WHERE id=?")
@SQLRestriction(value = "is_deleted=false")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType type;
    @Column(nullable = false)
    @ManyToMany
    private List<Rental> rental;
    @Column(nullable = false)
    private String sessionUrl;
    @Column(nullable = false)
    private String sessionId;
    @Column(nullable = false)
    private BigDecimal amountToPay;
    @Column(nullable = false)
    private boolean isDeleted = false;
}

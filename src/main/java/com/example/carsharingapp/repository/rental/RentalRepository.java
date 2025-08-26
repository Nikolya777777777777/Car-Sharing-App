package com.example.carsharingapp.repository.rental;

import com.example.carsharingapp.model.rental.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByUserIdAndActualReturnDateIsNull(Long userId);

    List<Rental> findByUserIdAndActualReturnDateIsNotNull(Long userId);

    @Query("SELECT o FROM Rental o WHERE o.user.id = :userId AND o.id = :rentalId")
    Optional<Rental> findByUserIdAndRentalId(@Param("userId") Long userId, @Param("rentalId") Long rentalId);
}

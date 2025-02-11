package com.example.identity_service.respository;

import com.example.identity_service.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {
    List<Rental> findByUserId(String userId);

    @Query("SELECT c.name, b, SUM(r.quantity) AS total_rented " +
            "FROM Rental r " +
            "JOIN r.book b " +
            "JOIN b.categories c " +
            "GROUP BY c.name, b " +
            "ORDER BY c.name ASC, total_rented DESC")
    List<Object[]> findMostRentedBooksByCategory();

    List<Rental> findByReturnedFalse();
}

package com.example.demo.repository;

import com.example.demo.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findByCountry(String country);
    List<Tour> findByCity(String city);
    List<Tour> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Tour> findByStartDateAfter(LocalDate date);
    List<Tour> findByAvailableSeatsGreaterThan(Integer seats);
    List<Tour> findByPriceBetween(BigDecimal min, BigDecimal max);

    @Query("SELECT t FROM Tour t WHERE LOWER(t.tourName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Tour> findByTourNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT t FROM Tour t WHERE t.startDate BETWEEN :start AND :end")
    List<Tour> findToursByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
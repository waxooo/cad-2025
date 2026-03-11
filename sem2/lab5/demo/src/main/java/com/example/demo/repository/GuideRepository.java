package com.example.demo.repository;

import com.example.demo.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
    Optional<Guide> findByEmail(String email);

    @Query("SELECT g FROM Guide g WHERE LOWER(g.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Guide> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

    List<Guide> findBySpecialization(String specialization);
    List<Guide> findByExperienceYearsGreaterThanEqual(Integer years);
}
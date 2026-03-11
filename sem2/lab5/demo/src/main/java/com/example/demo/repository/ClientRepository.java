package com.example.demo.repository;

import com.example.demo.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);
    Optional<Client> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Query("SELECT c FROM Client c WHERE LOWER(c.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Client> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

    @Query("SELECT c FROM Client c WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))")
    List<Client> findByFirstNameContainingIgnoreCase(@Param("firstName") String firstName);

    @Query("SELECT c FROM Client c WHERE c.phone LIKE CONCAT('%', :phone, '%')")
    List<Client> findByPhoneContaining(@Param("phone") String phone);

    @Query("SELECT c FROM Client c WHERE c.role.roleName = :roleName")
    List<Client> findByRoleName(@Param("roleName") String roleName);
}
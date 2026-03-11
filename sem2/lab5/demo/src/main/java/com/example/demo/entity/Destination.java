package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "destinations")
public class Destination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название страны обязательно")
    @Size(max = 100)
    @Column(name = "country", nullable = false, unique = true)
    private String country;

    @NotBlank(message = "Название города обязательно")
    @Size(max = 100)
    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "popular_attractions", columnDefinition = "TEXT")
    private String popularAttractions;

    @Column(name = "best_season")
    private String bestSeason;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPopularAttractions() { return popularAttractions; }
    public void setPopularAttractions(String popularAttractions) { this.popularAttractions = popularAttractions; }
    public String getBestSeason() { return bestSeason; }
    public void setBestSeason(String bestSeason) { this.bestSeason = bestSeason; }

    public String getFullName() {
        return city + ", " + country;
    }
}

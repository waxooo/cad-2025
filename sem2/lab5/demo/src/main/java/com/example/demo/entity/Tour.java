package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Column;

@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название тура обязательно")
    @Size(min = 5, max = 200)
    @Column(name = "tour_name", nullable = false)
    private String tourName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Стоимость обязательна")
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @NotNull(message = "Дата начала обязательна")
    @FutureOrPresent(message = "Дата начала должна быть в будущем или сегодня")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Дата окончания обязательна")
    @Future(message = "Дата окончания должна быть в будущем")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotBlank(message = "Страна назначения обязательна")
    @Size(max = 100)
    @Column(name = "country", nullable = false)
    private String country;

    @NotBlank(message = "Город назначения обязателен")
    @Size(max = 100)
    @Column(name = "city", nullable = false)
    private String city;

    @Min(value = 1, message = "Количество мест должно быть не менее 1")
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private Guide guide;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTourName() { return tourName; }
    public void setTourName(String tourName) { this.tourName = tourName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
    public Guide getGuide() { return guide; }
    public void setGuide(Guide guide) { this.guide = guide; }

    public String getTourInfo() {
        return tourName + " (" + country + ", " + city + ") " +
                startDate + " - " + endDate + " | " + price + " руб.";
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
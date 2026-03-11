package com.example.demo.controller;

import com.example.demo.entity.Tour;
import com.example.demo.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/tours")
public class TourController {

    @Autowired
    private TourRepository tourRepository;

    @GetMapping
    public String listTours(@RequestParam(required = false) String country,
                            @RequestParam(required = false) String city,
                            @RequestParam(required = false) Double minPrice,
                            @RequestParam(required = false) Double maxPrice,
                            Model model) {
        List<Tour> tours;

        // Фильтрация по параметрам
        if (country != null && !country.isEmpty()) {
            tours = tourRepository.findByCountry(country);
        } else if (city != null && !city.isEmpty()) {
            tours = tourRepository.findByCity(city);
        } else if (minPrice != null && maxPrice != null) {
            tours = tourRepository.findByPriceBetween(minPrice, maxPrice);
        } else {
            tours = tourRepository.findAll();
        }


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("userName", isAuthenticated ? auth.getName() : "");


        String userRole = "GUEST";
        if (isAuthenticated) {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            if (!authorities.isEmpty()) {

                String role = authorities.iterator().next().getAuthority();
                if (role.startsWith("ROLE_")) {
                    userRole = role.substring(5);
                } else {
                    userRole = role;
                }
            }
        }
        model.addAttribute("userRole", userRole);
        model.addAttribute("tours", tours);


        if (country != null || city != null || (minPrice != null && maxPrice != null)) {
            model.addAttribute("searchMessage", "Результаты поиска");
        }

        return "tours";
    }

    @GetMapping("/{id}")
    public String viewTour(@PathVariable Long id, Model model) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Тур не найден"));
        model.addAttribute("tour", tour);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "tour-details";
    }

    @GetMapping("/add")
    public String addTourForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("userName", isAuthenticated ? auth.getName() : "");

        // Определяем роль
        String userRole = "GUEST";
        if (isAuthenticated) {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            if (!authorities.isEmpty()) {
                String role = authorities.iterator().next().getAuthority();
                userRole = role.startsWith("ROLE_") ? role.substring(5) : role;
            }
        }
        model.addAttribute("userRole", userRole);
        return "add-tour";
    }

    @PostMapping("/add")
    public String addTour(
            @RequestParam String tourName,
            @RequestParam(required = false) String description,
            @RequestParam BigDecimal price,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam Integer availableSeats,
            RedirectAttributes redirectAttributes) {

        try {
            Tour tour = new Tour();
            tour.setTourName(tourName);
            tour.setDescription(description);
            tour.setPrice(price);
            tour.setStartDate(startDate);
            tour.setEndDate(endDate);
            tour.setCountry(country);
            tour.setCity(city);
            tour.setAvailableSeats(availableSeats);

            tourRepository.save(tour);
            redirectAttributes.addFlashAttribute("successMessage", "Тур «" + tourName + "» успешно добавлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении тура: " + e.getMessage());
            return "redirect:/tours/add";
        }

        return "redirect:/tours";
    }
}
package com.example.demo.controller;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Client;
import com.example.demo.entity.Tour;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private ClientRepository clientRepository;

    @PostMapping("/create")
    public String createBooking(@RequestParam Long tourId,
                                @RequestParam Integer participantsCount,
                                RedirectAttributes redirectAttributes) {
        try {
            // Текущий пользователь
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Client client = clientRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));

            // Тур
            Tour tour = tourRepository.findById(tourId)
                    .orElseThrow(() -> new RuntimeException("Тур не найден"));

            // Проверка мест
            if (tour.getAvailableSeats() < participantsCount) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Недостаточно мест. Доступно: " + tour.getAvailableSeats());
                return "redirect:/tours";
            }

            // Создание бронирования
            Booking booking = new Booking();
            booking.setClient(client);
            booking.setTour(tour);
            booking.setParticipantsCount(participantsCount);
            booking.setTotalPrice(tour.getPrice().multiply(BigDecimal.valueOf(participantsCount)));
            booking.setBookingDate(LocalDateTime.now());
            booking.setStatus(Booking.BookingStatus.CONFIRMED);

            bookingRepository.save(booking);

            // Обновление мест в туре
            tour.setAvailableSeats(tour.getAvailableSeats() - participantsCount);
            tourRepository.save(tour);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Вы успешно забронировали " + participantsCount + " мест(а) в туре «" + tour.getTourName() + "»");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }
        return "redirect:/tours";
    }
}
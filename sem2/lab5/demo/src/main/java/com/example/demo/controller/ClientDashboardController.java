package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Booking;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client")
public class ClientDashboardController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        List<Booking> bookings = bookingRepository.findByClientId(client.getId());

        long activeBookings = bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .count();

        model.addAttribute("client", client);
        model.addAttribute("bookings", bookings);
        model.addAttribute("orders", bookings);
        model.addAttribute("activeBookings", activeBookings);
        model.addAttribute("totalBookings", bookings.size());
        model.addAttribute("recentBookings", bookings.stream().limit(5).collect(Collectors.toList()));

        return "dashboard";
    }

    @PostMapping("/update-profile")
    public String updateProfile(
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            @RequestParam String lastName,
            @RequestParam String firstName,
            @RequestParam(required = false) String patronymic,
            @RequestParam String email,
            @RequestParam String phone,
            RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentEmail = auth.getName();

            Client client = clientRepository.findByEmail(currentEmail)
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));

            // Проверка изменения пароля
            if (currentPassword != null && !currentPassword.isEmpty()) {
                if (!passwordEncoder.matches(currentPassword, client.getPasswordHash())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Неверный текущий пароль");
                    return "redirect:/dashboard";
                }

                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Новые пароли не совпадают");
                    return "redirect:/dashboard";
                }

                client.setPasswordHash(passwordEncoder.encode(newPassword));
            }

            // Обновление профиля
            client.setLastName(lastName);
            client.setFirstName(firstName);
            client.setPatronymic(patronymic);
            client.setEmail(email);
            client.setPhone(phone);

            clientRepository.save(client);

            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно обновлен");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении профиля: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/order/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));
        model.addAttribute("order", booking);
        return "order-details";
    }
}

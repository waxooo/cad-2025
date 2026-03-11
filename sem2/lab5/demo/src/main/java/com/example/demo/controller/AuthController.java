package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Role;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("client")) {
            model.addAttribute("client", new Client());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Client client,
                           @RequestParam String confirmPassword,
                           RedirectAttributes redirectAttributes) {
        try {
            // Проверка паролей
            if (!client.getPasswordHash().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Пароли не совпадают");
                return "redirect:/register";
            }

            // Проверка уникальности email и телефона
            if (clientRepository.existsByEmail(client.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email уже зарегистрирован");
                return "redirect:/register";
            }
            if (clientRepository.existsByPhone(client.getPhone())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Телефон уже зарегистрирован");
                return "redirect:/register";
            }

            // Создание роли клиента
            Role clientRole = roleRepository.findByRoleName("CLIENT")
                    .orElseGet(() -> {
                        Role newRole = new Role("CLIENT", "Клиент");
                        return roleRepository.save(newRole);
                    });

            // Хеширование пароля
            client.setPasswordHash(passwordEncoder.encode(client.getPasswordHash()));
            client.setRole(clientRole);

            // Сохранение клиента
            clientRepository.save(client);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Регистрация успешна! Теперь вы можете войти в систему.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при регистрации: " + e.getMessage());
            return "redirect:/register";
        }
    }

    // Обработчик POST-запроса для логина
    @PostMapping("/login")
    public String handleLogin() {
        return "redirect:/client/dashboard";
    }
}

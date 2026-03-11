package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Role;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public String listClients(@RequestParam(required = false) String searchType,
                              @RequestParam(required = false) String searchValue,
                              Model model) {
        List<Client> clients;

        if (searchValue != null && !searchValue.isEmpty()) {
            switch (searchType) {
                case "lastName":
                    clients = clientRepository.findByLastNameContainingIgnoreCase(searchValue);
                    break;
                case "firstName":
                    clients = clientRepository.findByFirstNameContainingIgnoreCase(searchValue);
                    break;
                case "phone":
                    clients = clientRepository.findByPhoneContaining(searchValue);
                    break;
                default:
                    clients = clientRepository.findAll();
            }
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchValue", searchValue);
        } else {
            clients = clientRepository.findAll();
        }

        model.addAttribute("clients", clients);
        return "clients";
    }

    @PostMapping("/add")
    public String addClient(@RequestParam String lastName,
                            @RequestParam String firstName,
                            @RequestParam(required = false) String patronymic,
                            @RequestParam(required = false) String email,
                            @RequestParam String phone,
                            RedirectAttributes redirectAttributes) {
        try {
            Role clientRole = roleRepository.findByRoleName("CLIENT")
                    .orElseThrow(() -> new RuntimeException("Role CLIENT not found"));

            Client client = new Client();
            client.setLastName(lastName);
            client.setFirstName(firstName);
            client.setPatronymic(patronymic);
            client.setEmail(email != null && !email.isEmpty() ? email : lastName.toLowerCase() + firstName.toLowerCase() + "@example.com");
            client.setPhone(phone);
            client.setPasswordHash("password");
            client.setRole(clientRole);

            clientRepository.save(client);
            redirectAttributes.addFlashAttribute("successMessage", "Клиент успешно добавлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении клиента: " + e.getMessage());
        }
        return "redirect:/clients";
    }

    @GetMapping("/edit/{id}")
    public String editClientForm(@PathVariable Long id, Model model) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        model.addAttribute("client", client);
        return "clients";
    }

    @PostMapping("/update/{id}")
    public String updateClient(@PathVariable Long id,
                               @RequestParam String lastName,
                               @RequestParam String firstName,
                               @RequestParam(required = false) String patronymic,
                               @RequestParam(required = false) String email,
                               @RequestParam String phone,
                               RedirectAttributes redirectAttributes) {
        try {
            Client client = clientRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));

            client.setLastName(lastName);
            client.setFirstName(firstName);
            client.setPatronymic(patronymic);
            if (email != null && !email.isEmpty()) {
                client.setEmail(email);
            }
            client.setPhone(phone);

            clientRepository.save(client);
            redirectAttributes.addFlashAttribute("successMessage", "Клиент успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении клиента: " + e.getMessage());
        }
        return "redirect:/clients";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clientRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Клиент успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении клиента: " + e.getMessage());
        }
        return "redirect:/clients";
    }
}

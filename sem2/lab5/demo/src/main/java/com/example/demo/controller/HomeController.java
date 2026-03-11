package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Tour;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping({"/", "/home"})
    public String homePage(Model model) {
        List<Tour> tours = tourRepository.findAll();
        if (tours.size() > 3) {
            tours = tours.subList(0, 3);
        }

        model.addAttribute("tours", tours);
        addAuthAttributes(model);
        return "home";
    }

    private void addAuthAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("userName", email);

            try {
                Client client = clientRepository.findByEmail(email).orElse(null);
                if (client != null) {
                    model.addAttribute("userRole", client.getRole().getRoleName());
                }
            } catch (Exception e) {

            }
        } else {
            model.addAttribute("isAuthenticated", false);
        }
    }
}

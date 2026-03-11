package com.example.demo.controller;

import com.example.demo.entity.Guide;
import com.example.demo.repository.GuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/guides")
public class GuideController {

    @Autowired
    private GuideRepository guideRepository;

    @GetMapping
    public String listGuides(Model model) {
        List<Guide> guides = guideRepository.findAll();
        model.addAttribute("guides", guides);
        return "guides";
    }

    @GetMapping("/add")
    public String addGuideForm(Model model) {
        model.addAttribute("guide", new Guide());
        return "guides";
    }

    @PostMapping("/add")
    public String addGuide(@RequestParam String lastName,
                           @RequestParam String firstName,
                           @RequestParam(required = false) String email,
                           @RequestParam(required = false) String phone,
                           @RequestParam(required = false) Integer experienceYears,
                           @RequestParam(required = false) String specialization,
                           Model model) {
        Guide guide = new Guide();
        guide.setLastName(lastName);
        guide.setFirstName(firstName);
        guide.setEmail(email);
        guide.setPhone(phone);
        guide.setExperienceYears(experienceYears);
        guide.setSpecialization(specialization);
        guideRepository.save(guide);
        return "redirect:/guides";
    }
}

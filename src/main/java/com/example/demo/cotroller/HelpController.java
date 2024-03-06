package com.example.demo.cotroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class HelpController {

    private static final String PHONE = "+48 678 889 999";
    private static final String EMAIL = "info@g.com";

    @GetMapping("/")
    public String mainPage() {
        return "/home";
    }

    @GetMapping("/contact")
    public String showContactInfo(Model model) {
        model.addAttribute("phone", PHONE);
        model.addAttribute("email", EMAIL);
        return "/contact";
    }

}

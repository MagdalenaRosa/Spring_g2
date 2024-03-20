package com.example.demo.cotroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String userLogin(){
        return"/login/login";
    }
    @GetMapping("/logout")
    public String userLogout(){
        return "/login/logout";
    }
    
}

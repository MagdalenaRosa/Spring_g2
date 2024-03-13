package com.example.demo.cotroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.mappers.UserSaveDtoToUserMapper;
import com.example.demo.model.dto.UserSaveDto;
import com.example.demo.services.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
class UserController {
    final UserService userService;

    @GetMapping("/users")
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "/users/users";
    }

    @GetMapping("/user/id/{id}")
    public String showCurrentUser(Model model, @PathVariable Long id) {
        if (userService.findUserById(id).isPresent()) {
            model.addAttribute("user", userService.findUserById(id).get());
        }
        return "/users/user";

    }

    @GetMapping("/user/add")
    public String registerUser() {
        return "/users/save-user";
    }

    // edycja:
    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        if (userService.findUserById(id).isPresent()) {
            model.addAttribute("user", userService.findUserById(id).get());
        }
        return "/users/save-user";
    }

    @PostMapping("/user/save")
    public String insertUser(UserSaveDto userSaveDto) {
        var user = UserSaveDtoToUserMapper.fromUserDtoToUserEntity(userSaveDto);
        userService.saveUser(user);
        return "redirect:/users";
    }

}

package com.example.demo.cotroller;

import java.util.Arrays;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.AuthPasswordConfig;
import com.example.demo.mappers.UserSaveDtoToUserMapper;
import com.example.demo.model.Role;
import com.example.demo.model.dto.UserSaveDto;
import com.example.demo.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.var;

@Controller
@RequiredArgsConstructor
class UserController {
    final UserService userService;
    final AuthPasswordConfig authPasswordConfig;

    @GetMapping("/users")
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "/users/users";
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/user/id/{id}")
    public String showCurrentUser(Model model, @PathVariable Long id) {
        if (userService.findUserById(id).isPresent()) {
            model.addAttribute("user", userService.findUserById(id).get());
            return "/users/user";
        }else{
            model.addAttribute("error", "Edit current user with id="+id+" is NOT possible");
            model.addAttribute("errorAction", "/users");
     model.addAttribute("return", "Return to list of users");
                 return "/error-page";
        }


    }
    
    @GetMapping("/user/add")
    public String registerUser() {
        return "/users/save-user";
    }

    // edycja:
    @Secured({"ROLE_ADMIN"})
    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        if (userService.findUserById(id).isPresent()) {
            model.addAttribute("user", userService.findUserById(id).get());
            return "/users/save-user";
        }else{
            model.addAttribute("error", "Current user with id="+id+" doesn't exist");
     model.addAttribute("errorAction", "/users");
     model.addAttribute("return", "Return to list of users");
                 return "/error-page";
        }
        
    }
    @Secured({"ROLE_ADMIN"})
    @PostMapping("/user/update/{id}")
    public String userUpdate(@PathVariable Long id, @Valid UserSaveDto userSaveDto, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            var errors= bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/user/edit/"+id;
        }else{
            var user= UserSaveDtoToUserMapper.fromUserDtoToUserEntity(userSaveDto);
            userService.updateUser(id, user);
            return "redirect:/user/id/"+id;
        }

    }
    @Secured({"ROLE_ADMIN"})
    @PostMapping("/user/save")
    public String insertUser( @Valid UserSaveDto userSaveDto,BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        var user = UserSaveDtoToUserMapper.fromUserDtoToUserEntity(userSaveDto);
//  hash dla hasła
        user.setPassword(authPasswordConfig.passwordEncoder().encode(user.getPassword()));
        if(userService.existByEmail(userSaveDto.getEmail())){
            redirectAttributes.addFlashAttribute("errors","Email: "+ userSaveDto.getEmail()+" arleady exists");
            user.setEmail(null);
            redirectAttributes.addFlashAttribute("user",user);
            return "redirect:/user/add";
        }else if(bindingResult.hasErrors()){
            var errors= bindingResult.getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("user",user);
            return "redirect:/user/add";
        }else{
            user.setRole(Role.CLIENT);
            userService.saveUser(user);
            return "redirect:/";
        }
    }
    @Secured({"ROLE_ADMIN"})
    @GetMapping("/user/remove/{id}")
    public String removeUser(@PathVariable Long id){
        userService.removeUserById(id);
        return"redirect:/users";
    }
    @GetMapping("/user/role/{userId}")
    public String showChangeRoleForm(@PathVariable Long userId,Model model ){
        var user = userService.findUserById(userId).orElse(null);
        if(user==null){
            model.addAttribute("error", "User with id : "+userId+" doesn't exist");
            model.addAttribute("errorAction", "/users");
            model.addAttribute("return", "Return to list o users");

        }
        model.addAttribute("user", user);
        model.addAttribute("roles", Arrays.asList(Role.values()));
        return "/users/change-role";
    }
    @PostMapping("/user/role/{userId}")
    public  String changeUserRole(@PathVariable Long userId,@RequestParam String newRole ){
        Role role = Role.valueOf(newRole);
        userService.changeUserRole(userId, role);
        return "redirect:/user/id/"+ userId;
    }



}

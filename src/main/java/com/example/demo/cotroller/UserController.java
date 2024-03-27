package com.example.demo.cotroller;

import java.util.Arrays;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.AuthPasswordConfig;
import com.example.demo.exceptions.UserCanNotBeNullException;
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
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        if (userService.findUserById(id).isPresent()) {
            model.addAttribute("user", userService.findUserById(id).get());
            model.addAttribute("userRole", userService.findUserById(id).get().getRole().toString());
            return "/users/save-user";
        }else{
            model.addAttribute("error", "Current user with id="+id+" doesn't exist");
     model.addAttribute("errorAction", "/users");
     model.addAttribute("return", "Return to list of users");
                 return "/error-page";
        }
        
    }
 @PreAuthorize("isAuthenticated()")
    @PostMapping("/user/update/{id}")
    public String userUpdate(@PathVariable Long id, @Valid UserSaveDto userSaveDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Authentication authentication){
        if(bindingResult.hasErrors()){
            var errors= bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/user/edit/"+id;
        }else{
            var user= UserSaveDtoToUserMapper.fromUserDtoToUserEntity(userSaveDto);
            var optionalExistingUser=userService.findUserById(id);
            if(optionalExistingUser.isPresent()){
                var existsUser= optionalExistingUser.get();
                user.setRole(existsUser.getRole());
                userService.updateUser(id, user);
                if(authentication!=null && authentication.getAuthorities().stream().anyMatch(role-> role.getAuthority().equals("ROLE_ADMIN"))){
                    return "redirect:/user/id/"+id;
                }else{
                    redirectAttributes.addFlashAttribute("message","Succes update of current user");
                    return "redirect:/myAccount";
                }

            }else{
                return"/error-page";
            }

        }

    }
   
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
            // user.setRole(Role.CLIENT);
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
    @Secured({"ROLE_ADMIN"})
    @GetMapping("/user/role/{userId}")
    public String showChangeRoleForm(@PathVariable Long userId,Model model ){
        var user = userService.findUserById(userId).orElse(null);
        if(user==null){
            model.addAttribute("error", "User with id : "+userId+" doesn't exist");
            model.addAttribute("errorAction", "/users");
            model.addAttribute("return", "Return to list o users");
            return "/error-page";
        }
        model.addAttribute("user", user);
        model.addAttribute("roles", Arrays.asList(Role.values()));
        return "/users/change-role";
    }

    @PostMapping("/user/role/{userId}")
    public  String changeUserRole(@PathVariable Long userId,@RequestParam String newRole,Model model ){
        Role role = Role.valueOf(newRole);
        try {
            userService.changeUserRole(userId, role);
        } catch (UserCanNotBeNullException e) {
            model.addAttribute("error", "Change role for user with id : : "+userId+" is not possible");
            model.addAttribute("errorAction", "/users");
            model.addAttribute("return", "Return to list o users");
            return "/error-page";

        }
        return "redirect:/user/id/"+ userId;
    }
    @GetMapping("/myAccount")
    public String showFormToEditLoggedUser(Model model){

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails= (UserDetails) authentication.getPrincipal();

        String userEmail= userDetails.getUsername();

        var optionalUser=userService.findUserByEmail(userEmail);
        if(optionalUser.isPresent()){
            var user= optionalUser.get();
            model.addAttribute("user", user);
            model.addAttribute("userRole", user.getRole().toString());
            return "/users/save-user";
        }else{
            return"/error-page";
        }
    }



}

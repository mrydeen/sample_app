package com.smartgridz.controller;

import com.smartgridz.controller.dto.UserDto;
import com.smartgridz.domain.entity.User;
import com.smartgridz.service.UserService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Handler method to handle home page request
    @GetMapping(value = "/index")
    public String home(){
        return "index";
    }

    // handler method to handle login request
    @GetMapping("/login")
    public String loginForm(){
        return "login";
    }

    // handler method to handle user registration form request
    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        // create model object to store form data
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // Handler method to handle user registration form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model){
        User existingUser = userService.findUserByUserName(userDto.getUserName());

        if(existingUser != null && existingUser.getUserName() !=null && !existingUser.getUserName().isEmpty()){
            result.rejectValue("userName", null,
                    "There is already an account registered with the same user name");
        }

        if(result.hasErrors()){
            model.addAttribute("user", userDto);
            return "register";
        }

        userService.saveUser(userDto);
        return "redirect:/register?success";
    }

    // Handler method to delete a user from the system
    @PostMapping("/register/delete")
    public String deleteUser(@RequestParam(name = "username") String userName,
                               Model model){
        User existingUser = userService.findUserByUserName(userName);

        if(existingUser != null && existingUser.getUserName() !=null && !existingUser.getUserName().isEmpty()){
            return "redirect:/users?delete_failure";
        }

        userService.deleteUser(existingUser);
        return "redirect:/users?delete_success";
    }

    // handler method to handle list of users
    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
}

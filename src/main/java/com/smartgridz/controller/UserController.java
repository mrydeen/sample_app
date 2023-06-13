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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Handler method to handle list of users
    @GetMapping()
    public String users(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "user/users";
    }

    // handler method to handle user registration form request
    @GetMapping("/register")
    public String register(Model model){
        // create model object to store form data
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "user/register";
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

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "user/register";
        }

        userService.saveUser(userDto);
        return "redirect:/users/register?success";
    }

    // Handler method to delete a user from the system
    @PostMapping("/register/delete")
    public String deleteUser(@RequestParam(name = "username") String userName,
                               Model model) {
        User existingUser = userService.findUserByUserName(userName);

        if (existingUser == null) {
            LOG.error("Failed to find user \"{}\" to delete", userName);
            return "redirect:/users?delete_failure";
        }

        userService.deleteUser(existingUser);
        return "redirect:/users?delete_success";
    }


}

package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.BookService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BookService bookService;

    @Autowired
    public UserController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    // Show form to create a new user
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("books", bookService.findAllBooks());
        return "user-create"; // Thymeleaf template name
    }

    // Process the form to create a new user
    @PostMapping("/create")
    public String createUser(@ModelAttribute User user, @RequestParam Long bookId, Model model) {
        try {
            userService.createUser(user, bookId);
            return "redirect:/users/list";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Error creating user: " + e.getMessage());
            model.addAttribute("user", user); // Keep user input
            model.addAttribute("books", bookService.findAllBooks()); // Repopulate books
            return "user-create";
        }
    }

    // View details of a specific user
    @GetMapping("/{id}")
    public String findUserById(@PathVariable Long id, Model model) {
        Optional<User> userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "user-detail"; // Thymeleaf template name
        } else {
            model.addAttribute("errorMessage", "User not found with ID: " + id);
            return "error/404"; // Thymeleaf error template
        }
    }

    // List all users
    @GetMapping("/list")
    public String findAllUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "user-list"; // Thymeleaf template name
    }

    // Show form to edit an existing user
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Optional<User> userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            model.addAttribute("books", bookService.findAllBooks());
            return "user-edit"; // Thymeleaf template name
        } else {
            model.addAttribute("errorMessage", "User not found with ID: " + id + " for editing.");
            return "error/404"; // Thymeleaf error template
        }
    }

    // Process the form to update an existing user
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, @RequestParam Long bookId, Model model) {
        try {
            // user.setId(id); // UserService updateUser should handle finding by id
            userService.updateUser(id, user, bookId);
            return "redirect:/users/list";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Error updating user: " + e.getMessage());
            model.addAttribute("user", user); // Keep user input
            model.addAttribute("books", bookService.findAllBooks()); // Repopulate books
            return "user-edit";
        }
    }

    // Delete a user
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        try {
            userService.deleteUser(id);
            return "redirect:/users/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error deleting user with ID: " + id + ". " + e.getMessage());
            List<User> users = userService.findAllUsers();
            model.addAttribute("users", users);
            return "user-list"; // Or a specific error view
        }
    }
}

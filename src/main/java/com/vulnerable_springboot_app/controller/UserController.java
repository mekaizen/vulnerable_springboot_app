package com.vulnerable_springboot_app.controller;

import com.vulnerable_springboot_app.model.User;
import com.vulnerable_springboot_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    public User getUserByName(@RequestParam("name") String name) {
        // SQL Injection vulnerability
        return userRepository.findByNameQuery(name);
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam("input") String input) {
        model.addAttribute("userInput", input);  // Vulnerable to XSS
        return "index";
    }

    @PostMapping("/deserialize")
    public String deserialize(@RequestBody byte[] data) throws IOException, ClassNotFoundException {
        // Insecure deserialization vulnerability
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object obj = ois.readObject();
        return obj.toString();
    }


}

package com.vulnerable_springboot_app.controller;

import com.example.vulnerableapp.model.User;
import com.example.vulnerableapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

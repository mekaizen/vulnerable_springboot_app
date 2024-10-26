package com.vulnerable_springboot_app.controller;

import com.vulnerable_springboot_app.model.User;
import com.vulnerable_springboot_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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



    //Cross-Site Scripting (XSS)
    @GetMapping("/display")
    public String displayInput(@RequestParam("input") String input, Model model) {
        model.addAttribute("userInput", input); // Vulnerable to XSS
        return "display";
    }


    //Remote Code Execution (RCE)
    @PostMapping("/execute")
    public String executeCommand(@RequestParam("cmd") String cmd) throws IOException {
        // Directly executes user-supplied command, vulnerable to RCE
        Runtime.getRuntime().exec(cmd);
        return "Executed: " + cmd;
    }


    //File Upload Without Validation
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Path filePath = Paths.get("/uploads/" + file.getOriginalFilename());
        Files.write(filePath, file.getBytes());
        return "File uploaded successfully!";
    }

    @GetMapping("/xss")
    public String xssVulnerability(@RequestParam("input") String input) {
        return "<html><body>Input: " + input + "</body></html>";
    }


}

package com.vulnerable_springboot_app.controller;

import com.vulnerable_springboot_app.model.User;
import com.vulnerable_springboot_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RestController
public class VulnerableController {

    @Autowired
    private UserRepository userRepository;

    // 1. **SQL Injection** - directly inserting user input into query
    @GetMapping("/findUser")
    public User findUser(@RequestParam("username") String username) {
        String query = "SELECT * FROM users WHERE username = '" + username + "'"; // Vulnerable to SQL Injection
        return userRepository.findByNameQuery(query);
    }

    // 2. **Command Injection** - executing user-supplied commands
    @PostMapping("/execute")
    public String executeCommand(@RequestParam("cmd") String cmd) throws IOException {
        Runtime.getRuntime().exec(cmd); // Highly vulnerable to RCE
        return "Executed: " + cmd;
    }

    // 3. **Insecure Deserialization** - deserializing user-controlled input
    @PostMapping("/deserialize")
    public String deserialize_one(@RequestBody byte[] data) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data)); // Vulnerable to deserialization attack
        Object obj = ois.readObject();
        return obj.toString();
    }

    // 4. **File Upload Without Validation** - no security checks on file type, allowing malicious file uploads
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Path path = Paths.get("/tmp/uploads/" + file.getOriginalFilename());
        Files.write(path, file.getBytes()); // Potential for RCE if the file is executable
        return "File uploaded: " + file.getOriginalFilename();
    }

    // 5. **XSS Vulnerability** - directly reflecting user input in HTML
    @GetMapping("/xss")
    public String xss(@RequestParam("input") String input) {
        return "<html><body>User input: " + input + "</body></html>"; // Vulnerable to reflected XSS
    }

    // 6. **Open Redirect** - redirecting based on user input without validation
    @GetMapping("/redirect")
    public String redirect(@RequestParam("url") String url, HttpServletRequest request) {
        return "Redirecting to: <a href=\"" + url + "\">" + url + "</a>"; // Vulnerable to open redirect
    }

    // 7. **Path Traversal** - reading files based on user-controlled input
    @GetMapping("/readFile")
    public String readFile(@RequestParam("filename") String filename) throws IOException {
        Path path = Paths.get("/etc/" + filename); // Vulnerable to path traversal
        return new String(Files.readAllBytes(path));
    }

    // 8. **Remote File Inclusion** - accessing remote files based on user input
    @GetMapping("/includeFile")
    public String includeFile(@RequestParam("url") String url) throws IOException {
        InputStream inputStream = new URL(url).openStream(); // Vulnerable to RFI
        return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
    }

    // 9. **LDAP Injection** - injecting user input into an LDAP query without escaping
    @GetMapping("/ldapSearch")
    public String ldapSearch(@RequestParam("username") String username) {
        String ldapQuery = "(uid=" + username + ")"; // Vulnerable to LDAP injection
        // Perform LDAP search with ldapQuery...
        return "Searching for: " + ldapQuery;
    }

    // 10. **SSRF (Server-Side Request Forgery)** - allowing external requests based on user input
    @GetMapping("/fetch")
    public String fetch(@RequestParam("target") String target) throws IOException {
        URL url = new URL(target); // Vulnerable to SSRF
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        return reader.lines().collect(Collectors.joining("\n"));
    }
}

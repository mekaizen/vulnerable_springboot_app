package com.vulnerable_springboot_app.controller;

import com.vulnerable_springboot_app.model.User;
import com.vulnerable_springboot_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

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




    @GetMapping("/run-command")
    public String runCommand(@RequestParam String command) throws IOException {
        Runtime.getRuntime().exec(command); // Insecure command execution
        return "Command executed";
    }

    @GetMapping("/insecure-xss")
    public String insecureXss(@RequestParam String input) {
        return "<html><body>" + input + "</body></html>";
    }

    @GetMapping("/insecure-sql")
    public String insecureSql(@RequestParam String username) {
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        // Execute query without prepared statement (insecure)
        // Code here...
        return query;
    }

    @RequestMapping(value = "/vulnerable", method = RequestMethod.GET)
    public String vulnerableEndpoint(@RequestParam String input) {
        // Introducing a basic XSS vulnerability for testing purposes
        return "<html><body>Input is: " + input + "</body></html>";
    }

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



//
//    // 5. **XSS Vulnerability** - directly reflecting user input in HTML
//    @GetMapping("/xss")
//    public String xss(@RequestParam("input") String input) {
//        return "<html><body>User input: " + input + "</body></html>"; // Vulnerable to reflected XSS
//    }

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

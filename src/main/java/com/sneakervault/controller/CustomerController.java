package com.sneakervault.controller;

import com.sneakervault.dto.CustomerLoginRequest;
import com.sneakervault.dto.CustomerRegisterRequest;
import com.sneakervault.model.Customer;
import com.sneakervault.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CustomerRegisterRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank() || req.getPassword() == null || req.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }

        if (customerRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Email already registered"));
        }

        Customer customer = new Customer();
        customer.setEmail(req.getEmail().trim().toLowerCase());
        customer.setPasswordHash(hashPassword(req.getPassword()));
        customer.setName(req.getName());
        customer.setPhone(req.getPhone());
        customer.setZip(req.getZip());
        customer.setCity(req.getCity());
        customer.setStreet(req.getStreet());
        customer.setAuthToken(UUID.randomUUID().toString());
        customer.setRegisteredAt(LocalDateTime.now());

        customerRepository.save(customer);

        return ResponseEntity.ok(customerResponse(customer));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody CustomerLoginRequest req) {
        if (req.getEmail() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }

        Optional<Customer> opt = customerRepository.findByEmail(req.getEmail().trim().toLowerCase());
        if (opt.isEmpty() || !opt.get().getPasswordHash().equals(hashPassword(req.getPassword()))) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }

        Customer customer = opt.get();
        customer.setAuthToken(UUID.randomUUID().toString());
        customerRepository.save(customer);

        return ResponseEntity.ok(customerResponse(customer));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Customer customer = resolveCustomer(authHeader);
        if (customer == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(customerResponse(customer));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                           @RequestBody Map<String, String> body) {
        Customer customer = resolveCustomer(authHeader);
        if (customer == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        if (body.containsKey("name")) customer.setName(body.get("name"));
        if (body.containsKey("phone")) customer.setPhone(body.get("phone"));
        if (body.containsKey("zip")) customer.setZip(body.get("zip"));
        if (body.containsKey("city")) customer.setCity(body.get("city"));
        if (body.containsKey("street")) customer.setStreet(body.get("street"));

        customerRepository.save(customer);
        return ResponseEntity.ok(customerResponse(customer));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Customer customer = resolveCustomer(authHeader);
        if (customer == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        customer.setAuthToken(null);
        customerRepository.save(customer);
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listAll() {
        List<Customer> customers = customerRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Customer c : customers) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("email", c.getEmail());
            m.put("name", c.getName());
            m.put("phone", c.getPhone());
            m.put("zip", c.getZip());
            m.put("city", c.getCity());
            m.put("street", c.getStreet());
            m.put("registeredAt", c.getRegisteredAt());
            result.add(m);
        }
        return ResponseEntity.ok(result);
    }

    private Customer resolveCustomer(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        return customerRepository.findByAuthToken(token).orElse(null);
    }

    private Map<String, Object> customerResponse(Customer c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("token", c.getAuthToken());
        m.put("id", c.getId());
        m.put("email", c.getEmail());
        m.put("name", c.getName());
        m.put("phone", c.getPhone());
        m.put("zip", c.getZip());
        m.put("city", c.getCity());
        m.put("street", c.getStreet());
        m.put("registeredAt", c.getRegisteredAt());
        return m;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}

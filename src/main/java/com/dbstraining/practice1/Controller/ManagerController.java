package com.dbstraining.practice1.Controller;

import com.dbstraining.practice1.Model.Manager;
import com.dbstraining.practice1.Model.Requests;
import com.dbstraining.practice1.Model.User;
import com.dbstraining.practice1.Service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/managers")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Manager> createManager(@RequestBody Manager manager) {
        manager.setPassword(passwordEncoder.encode(manager.getPassword()));
        return ResponseEntity.ok(managerService.createManager(manager));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Manager>> getManagerById(@PathVariable Long id) {
        return ResponseEntity.ok(managerService.getManagerById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginAdmin(@RequestBody Map<String,String> credentials) {
        String email=credentials.get("email");
        String password=credentials.get("password");
        boolean isValid=managerService.loginAdmin(email,password);
        return isValid?ResponseEntity.ok("Login Successful "+email):ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Credentials");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Manager>> getAllManagers() {
        return ResponseEntity.ok(managerService.getAllManagers());
    }

    @GetMapping("/{managerId}/users")
    public ResponseEntity<List<User>> getUsersUnderManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(managerService.getUsersUnderManager(managerId));
    }

    @GetMapping("/{managerId}/requests")
    public ResponseEntity<List<Requests>> getRequestsForManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(managerService.getRequestsForManager(managerId));
    }

    @PostMapping("/approve-request/{requestId}/{managerId}")
    public ResponseEntity<Map<String,String>> approveRequest(@PathVariable Long requestId,@PathVariable Long managerId){
        managerService.approveRequest(requestId,managerId);
        return ResponseEntity.ok(Map.of("message","Request Approved"));
    }

    @PostMapping("/reject-request/{requestId}/{managerId}")
    public ResponseEntity<Map<String,String>> rejectRequest(@PathVariable Long requestId,@PathVariable Long managerId){
        managerService.rejectRequest(requestId,managerId);
        return ResponseEntity.ok(Map.of("message","Request Rejected"));
    }

    @GetMapping("/by-email/{email:.+}")
    public ResponseEntity<Manager> getManagerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(managerService.getByEmail(email));
    }
}
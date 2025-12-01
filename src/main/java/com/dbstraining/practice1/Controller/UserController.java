package com.dbstraining.practice1.Controller;

import com.dbstraining.practice1.Model.Requests;
import com.dbstraining.practice1.Model.User;
import com.dbstraining.practice1.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Map<String,String> credentials){
        String email=credentials.get("email");
        String password=credentials.get("password");
        boolean isValid=userService.loginUser(email,password);
        return isValid?ResponseEntity.status(HttpStatus.FOUND)
                .body("Login Successful"):ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Credentials");
    }

    @PostMapping("/{managerId}")
    public ResponseEntity<User> createUser(@RequestBody User user, @PathVariable Long managerId) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user, managerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/by-email/{email:.+}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getByEmail(email));
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<Requests>> getRequestsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getRequestsByUser(userId));
    }

    @DeleteMapping("/delete-request/{userId}")
    public String requestDeletion(@PathVariable Long userId) {
        userService.requestAccountDeletion(userId);
        return "Delete request has been sent to the admin";
    }
}

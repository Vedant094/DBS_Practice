package com.dbstraining.practice1.Controller;

import com.dbstraining.practice1.Security.JwtUtil;
import com.dbstraining.practice1.Security.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String password = body.get("password");

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // Load user details to extract role
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            // Generate token with ROLE included
            String token = jwtUtil.generateToken(email, role);

            return ResponseEntity.ok(
                    Map.of(
                            "token", token,
                            "role", role,
                            "email", email
                    )
            );

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid Credentials"));
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email){
        try{
            userDetailsService.loadUserByUsername(email);
            return ResponseEntity.ok(Map.of("exists",true));
        }
        catch(Exception e){
            return ResponseEntity.ok(Map.of("exists",false));
        }
    }
}

package com.dbstraining.practice1.Controller;

import com.dbstraining.practice1.Security.JwtUtil;
import com.dbstraining.practice1.Security.MyUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
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


    /* ============================
            LOGIN
       ============================ */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {

        String email = body.get("email");
        String password = body.get("password");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            // Generate tokens
            String accessToken = jwtUtil.generateAccessToken(email, role);
            String refreshToken = jwtUtil.generateRefreshToken(email, role);  // ★ FIXED ★

            // Cookie must be SameSite=None + secure for localhost:4200 → 8088
            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of(
                            "token", accessToken,
                            "refreshToken", refreshToken,
                            "role", role,
                            "email", email
                    ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid Credentials"));
        }
    }


    /* ============================
        REFRESH ACCESS TOKEN
       ============================ */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {

        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("refreshToken".equals(c.getName())) {
                    refreshToken = c.getValue();
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Refresh token missing"));
        }

        if (!jwtUtil.isTokenValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid refresh token"));
        }

        String email = jwtUtil.extractUsername(refreshToken);

        // Try to read role directly from refresh token
        String role = jwtUtil.extractRole(refreshToken);

        if (role == null) {
            UserDetails details = userDetailsService.loadUserByUsername(email);
            role = details.getAuthorities().iterator().next().getAuthority();
        }

        String newAccessToken = jwtUtil.generateAccessToken(email, role);

        return ResponseEntity.ok(Map.of("token", newAccessToken));
    }


    /* ============================
         CHECK EMAIL — RESTORED
       ============================ */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        try {
            userDetailsService.loadUserByUsername(email);
            return ResponseEntity.ok(Map.of("exists", true));
        }
        catch (Exception e) {
            return ResponseEntity.ok(Map.of("exists", false));
        }
    }


    /* ============================
                LOGOUT
       ============================ */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("msg", "Logged out"));
    }
}

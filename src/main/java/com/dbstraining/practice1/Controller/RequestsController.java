package com.dbstraining.practice1.Controller;

import com.dbstraining.practice1.Model.Requests;
import com.dbstraining.practice1.Service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestsController {

    private final RequestService requestService;

    @PostMapping("/{userId}")
    public ResponseEntity<Requests> createRequest(@RequestBody Requests request, @PathVariable Long userId) {
        return ResponseEntity.ok(requestService.createRequest(request, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Requests> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Requests>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Requests>> getRequestsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(requestService.getRequestsByUser(userId));
    }
}
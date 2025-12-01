package com.dbstraining.practice1.Service;

import com.dbstraining.practice1.Model.Requests;

import java.util.List;

public interface RequestService {
    Requests createRequest(Requests request, Long userId); // Assign request to a user
    Requests getRequestById(Long id);
    List<Requests> getAllRequests();
    List<Requests> getRequestsByUser(Long userId);
    Requests createDeletionRequest(Long userId);
}

package com.dbstraining.practice1.Service;

import com.dbstraining.practice1.Model.Manager;
import com.dbstraining.practice1.Model.Requests;
import com.dbstraining.practice1.Model.User;

import java.util.List;
import java.util.Optional;

public interface ManagerService {
    Manager createManager(Manager manager);
    Optional<Manager> getManagerById(Long managerId);
    //void deleteManager(Long ManagerId);

    void approveRequest(Long requestId,Long managerId);
    void rejectRequest(Long requestId,Long managerId);
    boolean loginAdmin(String email,String password);

    Manager getByEmail(String email);

//        Manager createManager(Manager manager);
//        Manager getManagerById(Long id);
        List<Manager> getAllManagers();
        List<User> getUsersUnderManager(Long managerId);
        List<Requests> getRequestsForManager(Long managerId);

}
